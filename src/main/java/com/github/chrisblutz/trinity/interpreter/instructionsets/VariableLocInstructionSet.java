package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;


/**
 * @author Christopher Lutz
 */
public class VariableLocInstructionSet {
    
    private TokenInfo[] tokens;
    
    public VariableLocInstructionSet(TokenInfo[] tokens) {
        
        this.tokens = tokens;
    }
    
    public TokenInfo[] getTokens() {
        
        return tokens;
    }
    
    public VariableLoc evaluate(TYObject thisObj, TYRuntime runtime) {
        
        if (tokens.length >= 1) {
            
            if (tokens[0].getToken() == Token.GLOBAL_VAR && tokens.length > 1 && tokens[1].getToken() == Token.NON_TOKEN_STRING) {
                
                String varName = tokens[1].getContents();
                
                if (Variables.hasGlobalVariable(varName)) {
                    
                    return Variables.getGlobalVariable(varName);
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.FieldNotFoundError", runtime, "Global field '" + varName + "' not found.");
                }
                
            } else if (tokens[0].getToken() == Token.NON_TOKEN_STRING) {
                
                String tokenContents = tokens[0].getContents();
                
                if (thisObj == TYObject.NONE) {
                    
                    if (runtime.hasVariable(tokenContents)) {
                        
                        return runtime.getVariableLoc(tokenContents);
                        
                    } else if (runtime.getThis().getObjectClass().hasVariable(tokenContents, runtime.getThis())) {
                        
                        return runtime.getThis().getObjectClass().getVariable(tokenContents, runtime.getThis());
                        
                    } else if (runtime.isStaticScope() && runtime.getScopeClass().hasVariable(tokenContents)) {
                        
                        return runtime.getScopeClass().getVariable(tokenContents);
                        
                    } else {
                        
                        VariableLoc newLoc = new VariableLoc();
                        VariableManager.put(newLoc, TYObject.NIL);
                        runtime.setVariableLoc(tokenContents, newLoc);
                        
                        return newLoc;
                    }
                    
                } else if (thisObj instanceof TYStaticClassObject) {
                    
                    TYStaticClassObject classObject = (TYStaticClassObject) thisObj;
                    TYClass tyClass = classObject.getInternalClass();
                    
                    if (tyClass.hasVariable(tokenContents)) {
                        
                        return tyClass.getVariable(tokenContents);
                    }
                    
                } else {
                    
                    TYClass tyClass = thisObj.getObjectClass();
                    if (tyClass.hasVariable(tokenContents, thisObj)) {
                        
                        return tyClass.getVariable(tokenContents, thisObj);
                    }
                }
            }
        }
        
        throwNoVarError(runtime);
        
        return null;
    }
    
    private void throwNoVarError(TYRuntime runtime) {
        
        Errors.throwError("Trinity.Errors.SyntaxError", runtime, "No variable found in this statement.");
    }
}
