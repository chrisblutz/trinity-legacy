package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeObject {
    
    static void register() {
        
        TrinityNatives.registerMethod("Object", "hashCode", false, null, null, null, (runtime, stackTrace, thisObj, params) -> thisObj == TYObject.NIL ? new TYInt(0) : new TYInt(thisObj.hashCode()));
        TrinityNatives.registerMethod("Object", "getClass", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYClassObject(thisObj.getObjectClass()));
        TrinityNatives.registerMethod("Object", "isInstance", false, new String[]{"instClass"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("instClass");
            
            if (object instanceof TYStaticClassObject) {
                
                TYClass tyClass = ((TYStaticClassObject) object).getInternalClass();
                
                return new TYBoolean(thisObj.getObjectClass().isInstanceOf(tyClass));
                
            } else if (object instanceof TYClassObject) {
                
                TYClass tyClass = ((TYClassObject) object).getInternalClass();
                
                return new TYBoolean(thisObj.getObjectClass().isInstanceOf(tyClass));
                
            } else {
                
                return TYBoolean.FALSE;
            }
        });
    }
}
