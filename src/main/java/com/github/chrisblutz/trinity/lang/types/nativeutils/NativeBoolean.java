package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeBoolean {
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.BOOLEAN, "TRUE", (runtime, thisObj, params) -> TYBoolean.TRUE);
        TrinityNatives.registerField(TrinityNatives.Classes.BOOLEAN, "FALSE", (runtime, thisObj, params) -> TYBoolean.FALSE);
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.BOOLEAN, "==", (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (object instanceof TYBoolean) {
                
                return TYBoolean.valueFor(TrinityNatives.cast(TYBoolean.class, thisObj).getInternalBoolean() == ((TYBoolean) object).getInternalBoolean());
            }
            
            return TYBoolean.FALSE;
        });
    }
}
