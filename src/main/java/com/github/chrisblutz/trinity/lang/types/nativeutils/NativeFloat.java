package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeFloat {
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.FLOAT, "NaN", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.NaN));
        TrinityNatives.registerField(TrinityNatives.Classes.FLOAT, "POSITIVE_INFINITY", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.POSITIVE_INFINITY));
        TrinityNatives.registerField(TrinityNatives.Classes.FLOAT, "NEGATIVE_INFINITY", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.NEGATIVE_INFINITY));
        TrinityNatives.registerField(TrinityNatives.Classes.FLOAT, "MIN_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.MIN_VALUE));
        TrinityNatives.registerField(TrinityNatives.Classes.FLOAT, "MAX_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.MAX_VALUE));
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.FLOAT, "isNaN", (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            return TYBoolean.valueFor(Double.isNaN(thisDouble));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.FLOAT, "isFinite", (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            return TYBoolean.valueFor(Double.isFinite(thisDouble));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.FLOAT, "isInfinite", (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            return TYBoolean.valueFor(Double.isInfinite(thisDouble));
        });
    }
}
