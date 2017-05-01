package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeString {
    
    static void register() {
        
        TrinityNatives.registerMethod("String", "chars", false, null, null, null, (runtime, stackTrace, thisObj, params) -> TrinityNatives.cast(TYString.class, thisObj, stackTrace).getCharacterArray());
        TrinityNatives.registerMethod("String", "+", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj, stackTrace).getInternalString();
            
            TYObject object = runtime.getVariable("other");
            String objStr = TrinityNatives.cast(TYString.class, object.tyInvoke("toString", runtime, stackTrace, null, null), stackTrace).getInternalString();
            
            return new TYString(thisString + objStr);
        });
        TrinityNatives.registerMethod("String", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            return new TYBoolean(TrinityNatives.cast(TYString.class, thisObj, stackTrace).getInternalString().contentEquals(TrinityNatives.cast(TYString.class, object, stackTrace).getInternalString()));
        });
    }
}
