package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class TYMethodObject extends TYObject {
    
    private TYMethod internalMethod;
    
    public TYMethodObject(TYMethod internal) {
        
        super(ClassRegistry.getClass("Method"));
        
        this.internalMethod = internal;
    }
    
    public TYMethod getInternalMethod() {
    
        return internalMethod;
    }
}
