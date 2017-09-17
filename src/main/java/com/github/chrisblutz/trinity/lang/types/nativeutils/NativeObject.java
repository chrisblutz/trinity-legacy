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
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "+", DefaultProcedures.getDefaultUOEOperationProcedure("+"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "-", DefaultProcedures.getDefaultUOEOperationProcedure("-"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "*", DefaultProcedures.getDefaultUOEOperationProcedure("*"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "/", DefaultProcedures.getDefaultUOEOperationProcedure("/"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "%", DefaultProcedures.getDefaultUOEOperationProcedure("%"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "<<", DefaultProcedures.getDefaultUOEOperationProcedure("<<"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, ">>", DefaultProcedures.getDefaultUOEOperationProcedure(">>"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, ">>>", DefaultProcedures.getDefaultUOEOperationProcedure(">>>"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "hashCode", (runtime, thisObj, params) -> NativeStorage.getHashCode(thisObj));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "getClass", (runtime, thisObj, params) -> NativeStorage.getClassObject(thisObj.getObjectClass()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "isInstance", (runtime, thisObj, params) -> {
            
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
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "respondsTo", (runtime, thisObj, params) -> {
            
            String method = TrinityNatives.toString(runtime.getVariable("method"), runtime);
            return TrinityNatives.getObjectFor(thisObj.getObjectClass().respondsTo(method));
        });
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.OBJECT, "checkReferences", (runtime, thisObj, params) -> {
            
            TYObject a = runtime.getVariable("a");
            TYObject b = runtime.getVariable("b");
            return TYBoolean.valueFor(a == b);
        });
    }
}
