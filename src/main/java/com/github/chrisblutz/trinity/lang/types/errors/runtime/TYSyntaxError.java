package com.github.chrisblutz.trinity.lang.types.errors.runtime;

import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYSyntaxError extends TYClass {
    
    public TYSyntaxError() {
        
        super("Trinity.Errors.SyntaxError", "SyntaxError", null);
    }
}
