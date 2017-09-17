package com.github.chrisblutz.trinity.lang.types.io;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.io.PrintStream;


/**
 * @author Christopher Lutz
 */
public class TYNativeOutputStream extends TYObject {
    
    private PrintStream internalStream;
    
    public TYNativeOutputStream(PrintStream internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.NATIVE_OUTPUT_STREAM));
        
        this.internalStream = internal;
    }
    
    public PrintStream getInternalStream() {
    
        return internalStream;
    }
}
