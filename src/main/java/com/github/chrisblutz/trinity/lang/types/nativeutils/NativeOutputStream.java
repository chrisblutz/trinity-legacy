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
        
        TrinityNatives.registerMethod("Trinity.IO.NativeOutputStream", "print", false, new String[]{"str"}, null, null, null, (runtime, thisObj, params) -> {
            
            PrintStream stream = TrinityNatives.cast(TYNativeOutputStream.class, thisObj).getInternalStream();
            String str = TrinityNatives.toString(runtime.getVariable("str"), runtime);
            stream.print(str);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.NativeOutputStream", "flush", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            PrintStream stream = TrinityNatives.cast(TYNativeOutputStream.class, thisObj).getInternalStream();
            stream.flush();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.NativeOutputStream", "close", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            PrintStream stream = TrinityNatives.cast(TYNativeOutputStream.class, thisObj).getInternalStream();
            stream.close();
            
            return TYObject.NONE;
        });
    }
}
