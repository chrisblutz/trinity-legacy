package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeBoolean {
    
    static void register() {
        
        TrinityNatives.registerField("Trinity.Boolean", "TRUE", (runtime, thisObj, params) -> TYBoolean.TRUE);
        TrinityNatives.registerField("Trinity.Boolean", "FALSE", (runtime, thisObj, params) -> TYBoolean.FALSE);
        
        TrinityNatives.registerMethod("Trinity.Boolean", "==", (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (object instanceof TYBoolean) {
                
                return TYBoolean.valueFor(TrinityNatives.cast(TYBoolean.class, thisObj).getInternalBoolean() == ((TYBoolean) object).getInternalBoolean());
            }
            
            return TYBoolean.FALSE;
        });
    }
}
