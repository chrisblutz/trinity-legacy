package com.github.chrisblutz.trinity.lang.types.threading;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYThreadObject extends TYObject {
    
    private TYThread internalThread;
    
    public TYThreadObject(TYThread internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.THREAD));
        
        this.internalThread = internal;
    }
    
    public TYThread getInternalThread() {
        
        return internalThread;
    }
}
