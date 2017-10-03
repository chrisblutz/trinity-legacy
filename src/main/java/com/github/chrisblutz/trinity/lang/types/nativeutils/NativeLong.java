package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeLong {
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.LONG, "MIN_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Long.MIN_VALUE));
        TrinityNatives.registerField(TrinityNatives.Classes.LONG, "MAX_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Long.MAX_VALUE));
    }
}
