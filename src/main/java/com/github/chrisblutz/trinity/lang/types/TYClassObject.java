package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYClassObject extends TYObject {
    
    private TYClass internalClass;
    
    public TYClassObject(TYClass internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.CLASS));
        
        this.internalClass = internal;
    }
    
    public TYClass getInternalClass() {
        
        return internalClass;
    }
}
