package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.DefaultProcedures;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeObject {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Object", "+", false, new String[]{"other"}, null, null, null, DefaultProcedures.getDefaultUOEOperationProcedure("+"));
        TrinityNatives.registerMethod("Trinity.Object", "-", false, new String[]{"other"}, null, null, null, DefaultProcedures.getDefaultUOEOperationProcedure("-"));
        TrinityNatives.registerMethod("Trinity.Object", "*", false, new String[]{"other"}, null, null, null, DefaultProcedures.getDefaultUOEOperationProcedure("*"));
        TrinityNatives.registerMethod("Trinity.Object", "/", false, new String[]{"other"}, null, null, null, DefaultProcedures.getDefaultUOEOperationProcedure("/"));
        TrinityNatives.registerMethod("Trinity.Object", "%", false, new String[]{"other"}, null, null, null, DefaultProcedures.getDefaultUOEOperationProcedure("%"));
        TrinityNatives.registerMethod("Trinity.Object", "==", false, new String[]{"other"}, null, null, null, (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            return TYBoolean.valueFor(thisObj == other);
        });
        TrinityNatives.registerMethod("Trinity.Object", "hashCode", false, null, null, null, null, (runtime, thisObj, params) -> NativeStorage.getHashCode(thisObj));
        TrinityNatives.registerMethod("Trinity.Object", "getClass", false, null, null, null, null, (runtime, thisObj, params) -> NativeStorage.getClassObject(thisObj.getObjectClass()));
        TrinityNatives.registerMethod("Trinity.Object", "isInstance", false, new String[]{"instClass"}, null, null, null, (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("instClass");
            
            if (object instanceof TYStaticClassObject) {
                
                TYClass tyClass = ((TYStaticClassObject) object).getInternalClass();
                
                return TYBoolean.valueFor(thisObj.getObjectClass().isInstanceOf(tyClass));
                
            } else if (object instanceof TYClassObject) {
                
                TYClass tyClass = ((TYClassObject) object).getInternalClass();
                
                return TYBoolean.valueFor(thisObj.getObjectClass().isInstanceOf(tyClass));
                
            } else {
                
                return TYBoolean.FALSE;
            }
        });
        TrinityNatives.registerMethod("Trinity.Object", "respondsTo", false, new String[]{"method"}, null, null, null, (runtime, thisObj, params) -> {
            
            String method = TrinityNatives.toString(runtime.getVariable("method"), runtime);
            return TrinityNatives.getObjectFor(thisObj.getObjectClass().respondsTo(method));
        });
    }
}
