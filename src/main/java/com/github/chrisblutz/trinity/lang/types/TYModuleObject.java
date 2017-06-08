package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class TYModuleObject extends TYObject {
    
    private TYModule internalModule;
    
    public TYModuleObject(TYModule internal) {
        
        super(ClassRegistry.getClass("Trinity.Module"));
        
        this.internalModule = internal;
    }
    
    public TYModule getInternalModule() {
        
        return internalModule;
    }
}
