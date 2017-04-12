package com.github.chrisblutz.trinity.lang.types.numeric;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class TYLong extends TYObject {
    
    private long internalLong;
    
    public TYLong(long internal) {
        
        super(ClassRegistry.getClass("Long"));
        
        this.internalLong = internal;
    }
    
    public long getInternalLong() {
        
        return internalLong;
    }
    
    public void setInternalLong(long internalLong) {
        
        this.internalLong = internalLong;
    }
}
