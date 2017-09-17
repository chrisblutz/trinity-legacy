package com.github.chrisblutz.trinity.lang.types.bool;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYBoolean extends TYObject {
    
    public static final TYBoolean TRUE = new TYBoolean(true), FALSE = new TYBoolean(false);
    
    private boolean internalBoolean;
    
    private TYBoolean(boolean internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.BOOLEAN));
        
        this.internalBoolean = internal;
    }
    
    public boolean getInternalBoolean() {
        
        return internalBoolean;
    }
    
    public static TYBoolean valueFor(boolean b) {
        
        if (b) {
            
            return TRUE;
            
        } else {
            
            return FALSE;
        }
    }
}
