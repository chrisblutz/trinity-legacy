package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.types.TYModuleObject;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeModule {
    
    static void register() {
        
        TrinityNatives.registerMethod("Module", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(((TYModuleObject) thisObj).getInternalModule().getName()));
    }
}
