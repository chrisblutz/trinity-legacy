package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeClass {
    
    static void register() {
        
        TrinityNatives.registerMethod("Class", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            if (thisObj instanceof TYClassObject) {
                
                return new TYString(((TYClassObject) thisObj).getInternalClass().getName());
                
            } else if (thisObj instanceof TYStaticClassObject) {
                
                return new TYString(((TYStaticClassObject) thisObj).getInternalClass().getName());
            }
            
            return new TYString("");
        });
        TrinityNatives.registerMethod("Class", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass thisClass = ((TYClassObject) thisObj).getInternalClass();
            TYClass otherClass;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYClassObject) {
                
                otherClass = ((TYClassObject) obj).getInternalClass();
                
            } else if (obj instanceof TYStaticClassObject) {
                
                otherClass = ((TYStaticClassObject) obj).getInternalClass();
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Method '==' takes a class object as a parameter.", stackTrace);
                error.throwError();
                
                otherClass = TYObject.NIL.getObjectClass();
            }
            
            return new TYBoolean(thisClass == otherClass);
        });
    }
}
