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
        
        TrinityNatives.registerMethod("Trinity.Object", "+", DefaultProcedures.getDefaultUOEOperationProcedure("+"));
        TrinityNatives.registerMethod("Trinity.Object", "-", DefaultProcedures.getDefaultUOEOperationProcedure("-"));
        TrinityNatives.registerMethod("Trinity.Object", "*", DefaultProcedures.getDefaultUOEOperationProcedure("*"));
        TrinityNatives.registerMethod("Trinity.Object", "/", DefaultProcedures.getDefaultUOEOperationProcedure("/"));
        TrinityNatives.registerMethod("Trinity.Object", "%", DefaultProcedures.getDefaultUOEOperationProcedure("%"));
        TrinityNatives.registerMethod("Trinity.Object", "<<", DefaultProcedures.getDefaultUOEOperationProcedure("<<"));
        TrinityNatives.registerMethod("Trinity.Object", ">>", DefaultProcedures.getDefaultUOEOperationProcedure(">>"));
        TrinityNatives.registerMethod("Trinity.Object", ">>>", DefaultProcedures.getDefaultUOEOperationProcedure(">>>"));
        TrinityNatives.registerMethod("Trinity.Object", "hashCode", (runtime, thisObj, params) -> NativeStorage.getHashCode(thisObj));
        TrinityNatives.registerMethod("Trinity.Object", "getClass", (runtime, thisObj, params) -> NativeStorage.getClassObject(thisObj.getObjectClass()));
        TrinityNatives.registerMethod("Trinity.Object", "isInstance", (runtime, thisObj, params) -> {
            
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
        TrinityNatives.registerMethod("Trinity.Object", "respondsTo", (runtime, thisObj, params) -> {
            
            String method = TrinityNatives.toString(runtime.getVariable("method"), runtime);
            return TrinityNatives.getObjectFor(thisObj.getObjectClass().respondsTo(method));
        });
        
        TrinityNatives.registerMethod("Trinity.Object", "checkReferences", (runtime, thisObj, params) -> {
            
            TYObject a = runtime.getVariable("a");
            TYObject b = runtime.getVariable("b");
            return TYBoolean.valueFor(a == b);
        });
    }
}
