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
        
        TrinityNatives.registerMethod("Class", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass thisClass = TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass();
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
        TrinityNatives.registerMethod("Class", "getSuperclass", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass superclass = TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getSuperclass();
            
            if (superclass == null) {
                
                return TYObject.NIL;
                
            } else {
                
                return NativeStorage.getClassObject(superclass);
            }
        });
        TrinityNatives.registerMethod("Class", "getModule", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYModule module = TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getModule();
            
            if (module != null) {
                
                return NativeStorage.getModuleObject(module);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Class", "getName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getClassName(TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass()));
        TrinityNatives.registerMethod("Class", "getShortName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getClassShortName(TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass()));
        TrinityNatives.registerMethod("Class", "construct", false, null, null, null, (runtime, stackTrace, thisObj, params) -> TrinityNatives.newInstance(TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getName(), runtime, stackTrace, params));
        TrinityNatives.registerMethod("Class", "getMethods", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> methods = new ArrayList<>();
            
            for (TYMethod m : TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getMethodArray()) {
                
                methods.add(NativeStorage.getMethodObject(m));
            }
            
            return new TYArray(methods);
        });
        TrinityNatives.registerMethod("Class", "getMethod", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYMethod method = TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getMethod(TrinityNatives.cast(TYString.class, runtime.getVariable("name"), stackTrace).getInternalString());
            
            if (method != null) {
                
                return NativeStorage.getMethodObject(method);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Class", "getInnerClasses", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> classes = new ArrayList<>();
            
            for (TYClass c : TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getClasses()) {
                
                classes.add(NativeStorage.getClassObject(c));
            }
            
            return new TYArray(classes);
        });
        TrinityNatives.registerMethod("Class", "getInnerClass", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass c = TrinityNatives.cast(TYClassObject.class, thisObj, stackTrace).getInternalClass().getClass(TrinityNatives.cast(TYString.class, runtime.getVariable("name"), stackTrace).getInternalString());
            
            if (c != null) {
                
                return NativeStorage.getClassObject(c);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Class", "get", true, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            String name = TrinityNatives.cast(TYString.class, runtime.getVariable("name"), stackTrace).getInternalString();
            
            if (ClassRegistry.classExists(name)) {
                
                return NativeStorage.getClassObject(ClassRegistry.getClass(name));
                
            } else {
                
                return TYObject.NIL;
            }
        });
    }
}
