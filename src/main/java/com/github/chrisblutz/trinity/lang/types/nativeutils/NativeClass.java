package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.errors.TYError;
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
        TrinityNatives.registerMethod("Class", "getSuperclass", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass superclass = ((TYClassObject) thisObj).getInternalClass().getSuperclass();
            
            if (superclass == null) {
                
                return TYObject.NIL;
                
            } else {
                
                return NativeStorage.getClassObject(superclass);
            }
        });
        TrinityNatives.registerMethod("Class", "getModule", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYModule module = ((TYClassObject) thisObj).getInternalClass().getModule();
            
            if (module != null) {
                
                return NativeStorage.getModuleObject(module);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Class", "getName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getClassName(((TYClassObject) thisObj).getInternalClass()));
        TrinityNatives.registerMethod("Class", "getShortName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getClassShortName(((TYClassObject) thisObj).getInternalClass()));
        TrinityNatives.registerMethod("Class", "construct", false, null, null, null, (runtime, stackTrace, thisObj, params) -> TrinityNatives.newInstance(((TYClassObject) thisObj).getInternalClass().getName(), runtime, stackTrace, params));
        TrinityNatives.registerMethod("Class", "getMethods", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> methods = new ArrayList<>();
            
            for (TYMethod m : ((TYClassObject) thisObj).getInternalClass().getMethodArray()) {
                
                methods.add(NativeStorage.getMethodObject(m));
            }
            
            return new TYArray(methods);
        });
        TrinityNatives.registerMethod("Class", "getMethod", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYMethod method = ((TYClassObject) thisObj).getInternalClass().getMethod(((TYString) runtime.getVariable("name")).getInternalString());
            
            if (method != null) {
                
                return NativeStorage.getMethodObject(method);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Class", "getInnerClasses", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> classes = new ArrayList<>();
            
            for (TYClass c : ((TYClassObject) thisObj).getInternalClass().getClasses()) {
                
                classes.add(NativeStorage.getClassObject(c));
            }
            
            return new TYArray(classes);
        });
        TrinityNatives.registerMethod("Class", "getInnerClass", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass c = ((TYClassObject) thisObj).getInternalClass().getClass(((TYString) runtime.getVariable("name")).getInternalString());
            
            if (c != null) {
                
                return NativeStorage.getClassObject(c);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Class", "get", true, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            String name = ((TYString) runtime.getVariable("name")).getInternalString();
            
            if (ClassRegistry.classExists(name)) {
                
                return NativeStorage.getClassObject(ClassRegistry.getClass(name));
                
            } else {
                
                return TYObject.NIL;
            }
        });
    }
}
