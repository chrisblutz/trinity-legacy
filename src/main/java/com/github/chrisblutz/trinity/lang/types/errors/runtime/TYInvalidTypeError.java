package com.github.chrisblutz.trinity.lang.types.errors.runtime;

import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYInvalidTypeError extends TYClass {
    
    public TYInvalidTypeError() {
        
        super("Trinity.Errors.InvalidTypeError", "InvalidTypeError", null);
    }
}
