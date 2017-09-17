package com.github.chrisblutz.trinity.lang.types.numeric;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYInt extends TYObject {
    
    private int internalInteger;
    
    public TYInt(int internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.INT));
        
        this.internalInteger = internal;
    }
    
    public int getInternalInteger() {
        
        return internalInteger;
    }
}
