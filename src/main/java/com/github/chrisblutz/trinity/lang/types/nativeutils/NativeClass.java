package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeClass {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Class", "==", (runtime, thisObj, params) -> {
            
            TYClass thisClass = TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass();
            TYClass otherClass;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYClassObject) {
                
                otherClass = ((TYClassObject) obj).getInternalClass();
                
            } else if (obj instanceof TYStaticClassObject) {
                
                otherClass = ((TYStaticClassObject) obj).getInternalClass();
                
            } else {
                
                return TYBoolean.FALSE;
            }
            
            return TYBoolean.valueFor(thisClass == otherClass);
        });
        TrinityNatives.registerMethod("Trinity.Class", "getSuperclass", (runtime, thisObj, params) -> {
            
            TYClass superclass = TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getSuperclass();
            
            if (superclass == null) {
                
                return TYObject.NIL;
                
            } else {
                
                return NativeStorage.getClassObject(superclass);
            }
        });
        TrinityNatives.registerMethod("Trinity.Class", "getModule", (runtime, thisObj, params) -> {
            
            TYModule module = TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getModule();
            
            if (module != null) {
                
                return NativeStorage.getModuleObject(module);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Trinity.Class", "getName", (runtime, thisObj, params) -> NativeStorage.getClassName(TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass()));
        TrinityNatives.registerMethod("Trinity.Class", "getShortName", (runtime, thisObj, params) -> NativeStorage.getClassShortName(TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass()));
        TrinityNatives.registerMethod("Trinity.Class", "construct", (runtime, thisObj, params) -> TrinityNatives.newInstance(TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getName(), runtime, params));
        TrinityNatives.registerMethod("Trinity.Class", "getMethods", (runtime, thisObj, params) -> {
            
            List<TYObject> methods = new ArrayList<>();
            
            for (TYMethod m : TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getMethodArray()) {
                
                methods.add(NativeStorage.getMethodObject(m));
            }
            
            return new TYArray(methods);
        });
        TrinityNatives.registerMethod("Trinity.Class", "getMethod", (runtime, thisObj, params) -> {
            
            TYMethod method = TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getMethod(TrinityNatives.cast(TYString.class, runtime.getVariable("name")).getInternalString());
            
            if (method != null) {
                
                return NativeStorage.getMethodObject(method);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Trinity.Class", "getInnerClasses", (runtime, thisObj, params) -> {
            
            List<TYObject> classes = new ArrayList<>();
            
            for (TYClass c : TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getClasses()) {
                
                classes.add(NativeStorage.getClassObject(c));
            }
            
            return new TYArray(classes);
        });
        TrinityNatives.registerMethod("Trinity.Class", "getInnerClass", (runtime, thisObj, params) -> {
            
            TYClass c = TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass().getClass(TrinityNatives.cast(TYString.class, runtime.getVariable("name")).getInternalString());
            
            if (c != null) {
                
                return NativeStorage.getClassObject(c);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Trinity.Class", "getComments", (runtime, thisObj, params) -> {
            
            TYClass c = TrinityNatives.cast(TYClassObject.class, thisObj).getInternalClass();
            
            return NativeStorage.getLeadingComments(c);
        });
        TrinityNatives.registerMethod("Trinity.Class", "get", (runtime, thisObj, params) -> {
            
            String name = TrinityNatives.cast(TYString.class, runtime.getVariable("name")).getInternalString();
            
            if (ClassRegistry.classExists(name)) {
                
                return NativeStorage.getClassObject(ClassRegistry.getClass(name));
                
            } else {
                
                return TYObject.NIL;
            }
        });
    }
}
