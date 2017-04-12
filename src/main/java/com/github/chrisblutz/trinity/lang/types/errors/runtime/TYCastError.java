package com.github.chrisblutz.trinity.lang.types.errors.runtime;

import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYCastError extends TYClass {
    
    public TYCastError() {
        
        super("Trinity.Errors.CastError", "CastError", null);
    }
}
