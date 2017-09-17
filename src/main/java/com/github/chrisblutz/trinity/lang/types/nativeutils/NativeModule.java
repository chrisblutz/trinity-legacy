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
    
    protected static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getModule", (runtime, thisObj, params) -> {
            
            TYModule module = TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule().getParentModule();
            
            if (module == null) {
                
                return TYObject.NIL;
            }
            
            return NativeStorage.getModuleObject(module);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getName", (runtime, thisObj, params) -> NativeStorage.getModuleName(TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getShortName", (runtime, thisObj, params) -> NativeStorage.getModuleShortName(TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getInnerModules", (runtime, thisObj, params) -> {
            
            List<TYObject> modules = new ArrayList<>();
            
            for (TYModule m : TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule().getModules()) {
                
                modules.add(NativeStorage.getModuleObject(m));
            }
            
            return new TYArray(modules);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getInnerModule", (runtime, thisObj, params) -> {
            
            TYModule m = TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule().getModule(TrinityNatives.cast(TYString.class, runtime.getVariable("name")).getInternalString());
            
            if (m == null) {
                
                return TYObject.NIL;
            }
            
            return NativeStorage.getModuleObject(m);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getInnerClasses", (runtime, thisObj, params) -> {
            
            List<TYObject> classes = new ArrayList<>();
            
            for (TYClass c : TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule().getClasses()) {
                
                classes.add(NativeStorage.getClassObject(c));
            }
            
            return new TYArray(classes);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getInnerClass", (runtime, thisObj, params) -> {
            
            TYClass c = TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule().getClass(TrinityNatives.cast(TYString.class, runtime.getVariable("name")).getInternalString());
            
            if (c == null) {
                
                return TYObject.NIL;
            }
            
            return NativeStorage.getClassObject(c);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "getComments", (runtime, thisObj, params) -> {
            
            TYModule m = TrinityNatives.cast(TYModuleObject.class, thisObj).getInternalModule();
            
            return NativeStorage.getLeadingComments(m);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "get", (runtime, thisObj, params) -> {
            
            String name = TrinityNatives.cast(TYString.class, runtime.getVariable("name")).getInternalString();
            
            if (ModuleRegistry.moduleExists(name)) {
                
                return NativeStorage.getModuleObject(ModuleRegistry.getModule(name));
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MODULE, "all", (runtime, thisObj, params) -> {
            
            List<TYObject> modules = new ArrayList<>();
            
            for (TYModule m : ModuleRegistry.getModules()) {
                
                modules.add(NativeStorage.getModuleObject(m));
            }
            
            return new TYArray(modules);
        });
    }
}
