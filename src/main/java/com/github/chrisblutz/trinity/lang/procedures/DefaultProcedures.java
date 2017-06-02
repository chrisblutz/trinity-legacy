package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.errors.Errors;


/**
 * @author Christopher Lutz
 */
public class DefaultProcedures {
    
    public static ProcedureAction getDefaultUOEOperationProcedure(String operation) {
        
        return (runtime, thisObj, params) -> {
            
            Errors.throwError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", runtime);
            
            return thisObj;
        };
    }
}
