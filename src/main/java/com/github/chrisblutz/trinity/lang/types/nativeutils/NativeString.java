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
        
        TrinityNatives.registerMethod("String", "chars", false, null, null, null, (runtime, stackTrace, thisObj, params) -> ((TYString) thisObj).getCharacterArray());
        TrinityNatives.registerMethod("String", "+", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            String thisString = ((TYString) thisObj).getInternalString();
            
            TYObject object = runtime.getVariable("other");
            String objStr = ((TYString) object.tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString();
            
            return new TYString(thisString + objStr);
        });
        TrinityNatives.registerMethod("String", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (object instanceof TYString) {
                
                return new TYBoolean(((TYString) thisObj).getInternalString().contentEquals(((TYString) object).getInternalString()));
            }
            
            return TYBoolean.FALSE;
        });
    }
}
