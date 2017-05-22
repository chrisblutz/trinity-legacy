package com.github.chrisblutz.trinity.lang.types.io;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;

import java.io.PrintStream;


/**
 * @author Christopher Lutz
 */
public class TYNativeOutputStream extends TYObject {
    
    private PrintStream internalStream;
    
    public TYNativeOutputStream(PrintStream internal) {
        
        super(ClassRegistry.getClass("Trinity.IO.NativeOutputStream"));
        
        this.internalStream = internal;
    }
    
    public PrintStream getInternalStream() {
    
        return internalStream;
    }
}
