package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.errors.TYError;


/**
 * @author Christopher Lutz
 */
public class DefaultProcedures {
    
    public static TYProcedure getDefaultUOEOperationProcedure(String operation) {
        
        return new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
            error.throwError();
            
            return thisObj;
        });
    }
}
