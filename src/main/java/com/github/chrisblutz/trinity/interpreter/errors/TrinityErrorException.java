package com.github.chrisblutz.trinity.interpreter.errors;

import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class TrinityErrorException extends RuntimeException {
    
    private TYObject errorObject;
    
    public TrinityErrorException(TYObject errorObject) {
        
        super(errorObject.getObjectClass().getName());
        this.errorObject = errorObject;
    }
    
    public TYObject getErrorObject() {
        
        return errorObject;
    }
}
