package com.github.chrisblutz.trinity.lang.types.numeric;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class TYFloat extends TYObject {
    
    private double internalDouble;
    
    public TYFloat(double internal) {
        
        super(ClassRegistry.getClass("Float"));
        
        this.internalDouble = internal;
    }
    
    public double getInternalDouble() {
        
        return internalDouble;
    }
}
