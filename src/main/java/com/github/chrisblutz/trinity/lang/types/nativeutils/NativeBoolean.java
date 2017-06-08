package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeBoolean {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Boolean", "==", false, new String[]{"other"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (object instanceof TYBoolean) {
                
                return TYBoolean.valueFor(TrinityNatives.cast(TYBoolean.class, thisObj).getInternalBoolean() == ((TYBoolean) object).getInternalBoolean());
            }
            
            return TYBoolean.FALSE;
        });
    }
}
