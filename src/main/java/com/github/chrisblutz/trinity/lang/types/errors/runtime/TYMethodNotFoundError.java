package com.github.chrisblutz.trinity.lang.types.errors.runtime;

import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYMethodNotFoundError extends TYClass {
    
    public TYMethodNotFoundError() {
        
        super("Trinity.Errors.MethodNotFoundError", "MethodNotFoundError", null);
    }
}
