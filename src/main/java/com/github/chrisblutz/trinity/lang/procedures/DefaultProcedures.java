package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYUnsupportedOperationError;


/**
 * @author Christopher Lutz
 */
public class DefaultProcedures {
    
    public static TYProcedure getDefaultUOEOperationProcedure(String operation) {
        
        return new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            TYError error = new TYError(new TYUnsupportedOperationError(), "Operation '" + operation + "' not supported.", stackTrace);
            error.throwError();
            
            return thisObj;
        });
    }
}
