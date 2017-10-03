package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeInt {
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.INT, "MIN_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Integer.MIN_VALUE));
        TrinityNatives.registerField(TrinityNatives.Classes.INT, "MAX_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Integer.MAX_VALUE));
    }
}
