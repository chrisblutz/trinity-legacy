package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class TYClassObject extends TYObject {
    
    private TYClass internalClass;
    
    public TYClassObject(TYClass internal) {
        
        super(ClassRegistry.getClass("Class"));
        
        this.internalClass = internal;
    }
    
    public TYClass getInternalClass() {
        
        return internalClass;
    }
}
