package com.github.chrisblutz.trinity.lang.types.threading;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.threading.TYThread;


/**
 * @author Christopher Lutz
 */
public class TYThreadObject extends TYObject {
    
    private TYThread internalThread;
    
    public TYThreadObject(TYThread internal) {
        
        super(ClassRegistry.getClass("Trinity.Thread"));
        
        this.internalThread = internal;
    }
    
    public TYThread getInternalThread() {
    
        return internalThread;
    }
}
