package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYStaticModuleObject extends TYObject {
    
    private TYModule internalModule;
    
    public TYStaticModuleObject(TYModule internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.MODULE));
        
        this.internalModule = internal;
    }
    
    public TYModule getInternalModule() {
        
        return internalModule;
    }
}
