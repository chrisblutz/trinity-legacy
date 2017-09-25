package com.github.chrisblutz.trinity.interpreter.actions;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;


/**
 * @author Christopher Lutz
 */
public class InterfaceMethodProcedureAction implements ProcedureAction {
    
    private String methodName;
    
    public InterfaceMethodProcedureAction(String methodName) {
        
        this.methodName = methodName;
    }
    
    @Override
    public TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params) {
        
        Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Cannot call interface method '" + methodName + "'.");
        return TYObject.NONE;
    }
}
