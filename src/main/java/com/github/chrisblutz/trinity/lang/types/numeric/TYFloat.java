package com.github.chrisblutz.trinity.lang.types.numeric;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYFloat extends TYObject {
    
    private double internalDouble;
    
    public TYFloat(double internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.FLOAT));
        
        this.internalDouble = internal;
    }
    
    public double getInternalDouble() {
        
        return internalDouble;
    }
}
