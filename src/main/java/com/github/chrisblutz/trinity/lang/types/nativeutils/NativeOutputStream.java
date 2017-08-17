package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.io.TYNativeOutputStream;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.io.PrintStream;


/**
 * @author Christopher Lutz
 */
class NativeOutputStream {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.IO.NativeOutputStream", "print", (runtime, thisObj, params) -> {
            
            PrintStream stream = TrinityNatives.cast(TYNativeOutputStream.class, thisObj).getInternalStream();
            String str = TrinityNatives.toString(runtime.getVariable("str"), runtime);
            stream.print(str);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.NativeOutputStream", "flush", (runtime, thisObj, params) -> {
            
            PrintStream stream = TrinityNatives.cast(TYNativeOutputStream.class, thisObj).getInternalStream();
            stream.flush();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.NativeOutputStream", "close", (runtime, thisObj, params) -> {
            
            PrintStream stream = TrinityNatives.cast(TYNativeOutputStream.class, thisObj).getInternalStream();
            stream.close();
            
            return TYObject.NONE;
        });
    }
}
