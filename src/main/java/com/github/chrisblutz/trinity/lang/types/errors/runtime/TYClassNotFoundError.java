package com.github.chrisblutz.trinity.lang.types.errors.runtime;

import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYClassNotFoundError extends TYClass {
    
    public TYClassNotFoundError() {
        
        super("Trinity.Errors.ClassNotFoundError", "ClassNotFoundError", null);
    }
}
