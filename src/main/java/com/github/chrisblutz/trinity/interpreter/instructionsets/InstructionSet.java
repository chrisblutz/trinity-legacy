package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYModuleObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticModuleObject;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class InstructionSet extends ObjectEvaluator {
    
    private TokenInfo[] tokens;
    private List<ChainedInstructionSet> children = new ArrayList<>();
    
    public InstructionSet(TokenInfo[] tokens, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        this.tokens = tokens;
    }
    
    public TokenInfo[] getTokens() {
        
        return tokens;
    }
    
    public void addChild(ChainedInstructionSet set) {
        
        children.add(set);
    }
    
    public List<ChainedInstructionSet> getChildren() {
        
        return children;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        if (tokens.length >= 1) {
            
            if (tokens[0].getToken() == Token.GLOBAL_VAR && tokens.length > 1 && tokens[1].getToken() == Token.NON_TOKEN_STRING) {
                
                String varName = tokens[1].getContents();
                
                if (Variables.hasGlobalVariable(varName)) {
                    
                    return VariableManager.getVariable(Variables.getGlobalVariable(varName));
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.FieldNotFoundError", runtime, "Global field '" + varName + "' not found.");
                }
                
            } else if (tokens[0].getToken() == Token.NON_TOKEN_STRING) {
                
                String tokenContents = tokens[0].getContents();
                
                if (thisObj == TYObject.NONE) {
                    
                    if (tokenContents.contentEquals("this")) {
                        
                        return runtime.getThis();
                        
                    } else if (runtime.hasVariable(tokenContents)) {
                        
                        return runtime.getVariable(tokenContents);
                        
                    } else if (runtime.getThis() != TYObject.NONE && runtime.getThis().getObjectClass().hasVariable(tokenContents, runtime.getThis())) {
                        
                        TYClass tyClass = runtime.getTyClass();
                        VariableLoc loc = tyClass.getVariable(tokenContents, runtime.getThis());
                        
                        if (tyClass.checkScope(loc, runtime)) {
                            
                            return VariableManager.getVariable(loc);
                            
                        } else {
                            
                            Errors.throwError("Trinity.Errors.ScopeError", "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                        }
                        
                    } else if (runtime.isStaticScope() && runtime.getScopeClass().hasVariable(tokenContents)) {
                        
                        TYClass tyClass = runtime.getScopeClass();
                        VariableLoc loc = tyClass.getVariable(tokenContents);
                        
                        if (tyClass.checkScope(loc, runtime)) {
                            
                            return VariableManager.getVariable(loc);
                            
                        } else {
                            
                            Errors.throwError("Trinity.Errors.ScopeError", "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                        }
                        
                    } else if (runtime.getModule() != null && runtime.getModule().hasClass(tokenContents)) {
                        
                        return NativeStorage.getStaticClassObject(runtime.getModule().getClass(tokenContents));
                        
                    } else if (runtime.hasImportedModuleWithClass(tokenContents)) {
                        
                        return NativeStorage.getStaticClassObject(runtime.getImportedClassWithModule(tokenContents));
                        
                    } else if (ModuleRegistry.moduleExists(tokenContents)) {
                        
                        return NativeStorage.getStaticModuleObject(ModuleRegistry.getModule(tokenContents));
                        
                    } else if (ClassRegistry.classExists(tokenContents)) {
                        
                        return NativeStorage.getStaticClassObject(ClassRegistry.getClass(tokenContents));
                        
                    } else if (ModuleRegistry.getModule("Trinity").hasClass(tokenContents)) {
                        
                        return NativeStorage.getStaticClassObject(ModuleRegistry.getModule("Trinity").getClass(tokenContents));
                        
                    } else {
                        
                        List<TYObject> params = new ArrayList<>();
                        
                        for (ChainedInstructionSet set : getChildren()) {
                            
                            TYObject obj;
                            
                            obj = set.evaluate(TYObject.NONE, runtime);
                            
                            if (obj != TYObject.NONE) {
                                
                                params.add(obj);
                            }
                        }
                        
                        if (runtime.isStaticScope()) {
                            
                            return runtime.getScopeClass().tyInvoke(tokenContents, runtime, getProcedure(), runtime, TYObject.NONE, params.toArray(new TYObject[params.size()]));
                            
                        } else {
                            
                            return runtime.getScope().tyInvoke(tokenContents, runtime, getProcedure(), runtime, params.toArray(new TYObject[params.size()]));
                        }
                    }
                    
                } else if (thisObj instanceof TYStaticModuleObject) {
                    
                    TYStaticModuleObject moduleObject = (TYStaticModuleObject) thisObj;
                    TYModule tyModule = moduleObject.getInternalModule();
                    
                    if (tyModule.hasModule(tokenContents)) {
                        
                        return NativeStorage.getStaticModuleObject(tyModule.getModule(tokenContents));
                        
                    } else if (tyModule.hasClass(tokenContents)) {
                        
                        return NativeStorage.getStaticClassObject(tyModule.getClass(tokenContents));
                    }
                    
                } else if (thisObj instanceof TYModuleObject) {
                    
                    List<TYObject> params = new ArrayList<>();
                    
                    for (ChainedInstructionSet set : getChildren()) {
                        
                        TYObject obj = set.evaluate(TYObject.NONE, runtime);
                        
                        if (obj != TYObject.NONE) {
                            
                            params.add(obj);
                        }
                    }
                    
                    return thisObj.tyInvoke(tokenContents, runtime, getProcedure(), runtime, params.toArray(new TYObject[params.size()]));
                    
                } else if (thisObj instanceof TYStaticClassObject) {
                    
                    TYStaticClassObject classObject = (TYStaticClassObject) thisObj;
                    TYClass tyClass = classObject.getInternalClass();
                    
                    tyClass.runInitializationActions();
                    
                    if (tyClass.hasVariable(tokenContents)) {
                        
                        VariableLoc loc = tyClass.getVariable(tokenContents);
                        
                        if (tyClass.checkScope(loc, runtime)) {
                            
                            if (loc == null) {
                                
                                Errors.throwError("Trinity.Errors.FieldNotFoundError", "No field '" + tokenContents + "' found.", runtime);
                            }
                            
                            return loc.getValue();
                            
                        } else {
                            
                            Errors.throwError("Trinity.Errors.ScopeError", "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                        }
                        
                    } else if (tyClass.hasClass(tokenContents)) {
                        
                        return NativeStorage.getStaticClassObject(tyClass.getClass(tokenContents));
                        
                    } else {
                        
                        List<TYObject> params = new ArrayList<>();
                        
                        for (ChainedInstructionSet set : getChildren()) {
                            
                            TYObject obj = set.evaluate(TYObject.NONE, runtime);
                            
                            if (obj != TYObject.NONE) {
                                
                                params.add(obj);
                            }
                        }
                        
                        return classObject.getInternalClass().tyInvoke(tokenContents, runtime, getProcedure(), runtime, TYObject.NONE, params.toArray(new TYObject[params.size()]));
                    }
                    
                } else {
                    
                    if (thisObj != TYObject.NONE) {
                        
                        TYClass tyClass = thisObj.getObjectClass();
                        if (tyClass.hasVariable(tokenContents, thisObj)) {
                            
                            VariableLoc loc = tyClass.getVariable(tokenContents, thisObj);
                            
                            if (tyClass.checkScope(loc, runtime)) {
                                
                                if (loc == null) {
                                    
                                    Errors.throwError("Trinity.Errors.FieldNotFoundError", "No field '" + tokenContents + "' found.", runtime);
                                }
                                
                                return loc.getValue();
                                
                            } else {
                                
                                Errors.throwError("Trinity.Errors.ScopeError", "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                            }
                            
                        } else {
                            
                            List<TYObject> params = new ArrayList<>();
                            for (ChainedInstructionSet set : getChildren()) {
                                
                                TYObject obj = set.evaluate(TYObject.NONE, runtime);
                                
                                if (obj != TYObject.NONE) {
                                    
                                    params.add(obj);
                                }
                            }
                            
                            return thisObj.tyInvoke(tokenContents, runtime, getProcedure(), runtime, params.toArray(new TYObject[params.size()]));
                        }
                    }
                }
            }
        }
        
        return TYObject.NONE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "InstructionSet [");
        
        for (TokenInfo info : getTokens()) {
            
            str.append(info.getContents());
        }
        
        str.append("]");
        
        for (ObjectEvaluator child : getChildren()) {
            
            str.append("\n").append(indent).append(child.toString(indent + "\t"));
        }
        
        return str.toString();
    }
}
