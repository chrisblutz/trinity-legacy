package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.ModuleRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.TYModuleObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeModule {
    
    static void register() {
        
        TrinityNatives.registerMethod("Module", "getModule", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYModule module = TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule().getParentModule();
            
            if (module != null) {
                
                return NativeStorage.getModuleObject(module);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Module", "getName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getModuleName(TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule()));
        TrinityNatives.registerMethod("Module", "getShortName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getModuleShortName(TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule()));
        TrinityNatives.registerMethod("Module", "getInnerModules", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> modules = new ArrayList<>();
            
            for (TYModule m : TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule().getModules()) {
                
                modules.add(NativeStorage.getModuleObject(m));
            }
            
            return new TYArray(modules);
        });
        TrinityNatives.registerMethod("Module", "getInnerModule", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYModule m = TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule().getModule(TrinityNatives.cast(TYString.class, runtime.getVariable("name"), stackTrace).getInternalString());
            
            if (m != null) {
                
                return NativeStorage.getModuleObject(m);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Module", "getInnerClasses", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> classes = new ArrayList<>();
            
            for (TYClass c : TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule().getClasses()) {
                
                classes.add(NativeStorage.getClassObject(c));
            }
            
            return new TYArray(classes);
        });
        TrinityNatives.registerMethod("Module", "getInnerClass", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass c = TrinityNatives.cast(TYModuleObject.class, thisObj, stackTrace).getInternalModule().getClass(TrinityNatives.cast(TYString.class, runtime.getVariable("name"), stackTrace).getInternalString());
            
            if (c != null) {
                
                return NativeStorage.getClassObject(c);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Module", "get", true, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            String name = TrinityNatives.cast(TYString.class, runtime.getVariable("name"), stackTrace).getInternalString();
            
            if (ModuleRegistry.moduleExists(name)) {
                
                return NativeStorage.getModuleObject(ModuleRegistry.getModule(name));
                
            } else {
                
                return TYObject.NIL;
            }
        });
    }
}
