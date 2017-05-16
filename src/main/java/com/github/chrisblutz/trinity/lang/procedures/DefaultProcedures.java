package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.errors.TYError;


/**
 * @author Christopher Lutz
 */
public class DefaultProcedures {
    
    public static TYProcedure getDefaultUOEOperationProcedure(String operation) {
        
        return new TYProcedure((runtime, thisObj, params) -> {
            
            TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.");
            error.throwError();
            
            return thisObj;
        });
    }
}
