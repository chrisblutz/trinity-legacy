package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;


/**
 * @author Christopher Lutz
 */
public class SingleTokenVariableLocRetriever implements VariableLocRetriever {
    
    private String contents;
    
    public SingleTokenVariableLocRetriever(String contents) {
        
        this.contents = contents;
    }
    
    public String getContents() {
        
        return contents;
    }
    
    @Override
    public VariableLoc evaluate(TYObject thisObj, TYRuntime runtime) {
        
        if (thisObj == TYObject.NONE) {
            
            if (runtime.hasVariable(getContents())) {
                
                return runtime.getVariableLoc(getContents());
                
            } else if (runtime.getThis() != TYObject.NONE && runtime.getThis().getObjectClass().hasVariable(getContents(), runtime.getThis())) {
                
                return runtime.getTyClass().getVariable(getContents(), runtime.getThis());
                
            } else if (runtime.isStaticScope() && runtime.getScopeClass().hasVariable(getContents())) {
                
                return runtime.getScopeClass().getVariable(getContents());
                
            } else {
                
                VariableLoc newLoc = new VariableLoc();
                VariableManager.put(newLoc, TYObject.NIL);
                runtime.setVariableLoc(getContents(), newLoc);
                
                return newLoc;
            }
            
        } else if (thisObj instanceof TYStaticClassObject) {
            
            TYStaticClassObject classObject = (TYStaticClassObject) thisObj;
            TYClass tyClass = classObject.getInternalClass();
            
            tyClass.runInitializationActions();
            
            if (tyClass.hasVariable(getContents())) {
                
                return tyClass.getVariable(getContents());
            }
            
        } else {
            
            TYClass tyClass = thisObj.getObjectClass();
            if (tyClass.hasVariable(getContents(), thisObj)) {
                
                return tyClass.getVariable(getContents(), thisObj);
            }
        }
        
        Errors.throwError("Trinity.Errors.FieldNotFoundError", runtime, "No field '" + getContents() + "' found.");
        
        return null;
    }
}
