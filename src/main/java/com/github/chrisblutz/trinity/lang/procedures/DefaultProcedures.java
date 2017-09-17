package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.errors.Errors;


/**
 * @author Christopher Lutz
 */
public class DefaultProcedures {
    
    public static ProcedureAction getDefaultUOEOperationProcedure(String operation) {
        
        return (runtime, thisObj, params) -> {
            
            Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, runtime, "Operation '" + operation + "' not supported.");
            
            return thisObj;
        };
    }
}
