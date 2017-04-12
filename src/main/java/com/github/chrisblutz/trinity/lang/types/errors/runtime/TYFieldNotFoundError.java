package com.github.chrisblutz.trinity.lang.types.errors.runtime;

import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYFieldNotFoundError extends TYClass {
    
    public TYFieldNotFoundError() {
        
        super("Trinity.Errors.FieldNotFoundError", "FieldNotFoundError", null);
    }
}
