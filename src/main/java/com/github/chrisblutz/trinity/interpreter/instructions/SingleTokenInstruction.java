package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticModuleObject;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.natives.NativeStorage;


/**
 * @author Christopher Lutz
 */
public class SingleTokenInstruction extends Instruction {
    
    private String contents;
    
    public SingleTokenInstruction(String contents, Location location) {
        
        super(location);
        
        this.contents = contents;
    }
    
    public String getContents() {
        
        return contents;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        if (thisObj == TYObject.NONE) {
            
            if (runtime.hasVariable(getContents())) {
                
                return runtime.getVariable(getContents());
                
            } else if (runtime.getThis() != TYObject.NONE && runtime.getThis().getObjectClass().hasVariable(getContents(), runtime.getThis())) {
                
                TYClass tyClass = runtime.getTyClass();
                VariableLoc loc = tyClass.getVariable(getContents(), runtime.getThis());
                
                if (loc.checkScope(runtime)) {
                    
                    return VariableManager.getVariable(loc);
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.ScopeError", runtime, "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                }
                
            } else if (runtime.isStaticScope() && runtime.getScopeClass().hasVariable(getContents())) {
                
                TYClass tyClass = runtime.getScopeClass();
                VariableLoc loc = tyClass.getVariable(getContents());
                
                if (loc.checkScope(runtime)) {
                    
                    return VariableManager.getVariable(loc);
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.ScopeError", runtime, "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                }
                
            } else if (runtime.getModule() != null && runtime.getModule().hasClass(getContents())) {
                
                return NativeStorage.getStaticClassObject(runtime.getModule().getClass(getContents()));
                
            } else if (runtime.hasImportedModuleWithClass(getContents())) {
                
                return NativeStorage.getStaticClassObject(runtime.getImportedClassWithModule(getContents()));
                
            } else if (ModuleRegistry.moduleExists(getContents())) {
                
                return NativeStorage.getStaticModuleObject(ModuleRegistry.getModule(getContents()));
                
            } else if (ClassRegistry.classExists(getContents())) {
                
                return NativeStorage.getStaticClassObject(ClassRegistry.getClass(getContents()));
                
            } else if (ModuleRegistry.getModule("Trinity").hasClass(getContents())) {
                
                return NativeStorage.getStaticClassObject(ModuleRegistry.getModule("Trinity").getClass(getContents()));
            }
            
        } else if (thisObj instanceof TYStaticModuleObject) {
            
            TYStaticModuleObject moduleObject = (TYStaticModuleObject) thisObj;
            TYModule tyModule = moduleObject.getInternalModule();
            
            if (tyModule.hasModule(getContents())) {
                
                return NativeStorage.getStaticModuleObject(tyModule.getModule(getContents()));
                
            } else if (tyModule.hasClass(getContents())) {
                
                return NativeStorage.getStaticClassObject(tyModule.getClass(getContents()));
            }
            
        } else if (thisObj instanceof TYStaticClassObject) {
            
            TYStaticClassObject classObject = (TYStaticClassObject) thisObj;
            TYClass tyClass = classObject.getInternalClass();
            
            tyClass.runInitializationActions();
            
            if (tyClass.hasVariable(getContents())) {
                
                VariableLoc loc = tyClass.getVariable(getContents());
                
                if (loc.checkScope(runtime)) {
                    
                    return loc.getValue();
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.ScopeError", runtime, "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                }
                
            } else if (tyClass.hasClass(getContents())) {
                
                return NativeStorage.getStaticClassObject(tyClass.getClass(getContents()));
            }
            
        } else {
            
            TYClass tyClass = thisObj.getObjectClass();
            if (tyClass.hasVariable(getContents(), thisObj)) {
                
                VariableLoc loc = tyClass.getVariable(getContents(), thisObj);
                
                if (loc.checkScope(runtime)) {
                    
                    return loc.getValue();
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.ScopeError", runtime, "Cannot access value of field marked '" + loc.getScope().toString() + "' here.");
                }
            }
        }
        
        Errors.throwError("Trinity.Errors.FieldNotFoundError", runtime, "No field '" + getContents() + "' found.");
        
        return TYObject.NIL;
    }
}
