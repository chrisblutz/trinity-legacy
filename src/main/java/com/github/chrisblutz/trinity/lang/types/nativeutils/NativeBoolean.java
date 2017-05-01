package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeBoolean {
    
    static void register() {
        
        TrinityNatives.registerMethod("Boolean", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(Boolean.toString(TrinityNatives.cast(TYBoolean.class, thisObj, stackTrace).getInternalBoolean())));
        TrinityNatives.registerMethod("Boolean", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (object instanceof TYBoolean) {
                
                return new TYBoolean(TrinityNatives.cast(TYBoolean.class, thisObj, stackTrace).getInternalBoolean() == ((TYBoolean) object).getInternalBoolean());
            }
            
            return TYBoolean.FALSE;
        });
    }
}
