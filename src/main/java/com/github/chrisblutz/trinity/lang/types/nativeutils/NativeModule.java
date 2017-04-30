package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.ModuleRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYModuleObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticModuleObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeModule {
    
    static void register() {
        
        TrinityNatives.registerMethod("Module", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            if (thisObj instanceof TYModuleObject) {
                
                return new TYString(((TYModuleObject) thisObj).getInternalModule().getName());
                
            } else if (thisObj instanceof TYStaticModuleObject) {
                
                return new TYString(((TYStaticModuleObject) thisObj).getInternalModule().getName());
            }
            
            return new TYString("");
        });
        TrinityNatives.registerMethod("Module", "getModule", false, null, null, null, new ProcedureAction() {
            
            @Override
            public TYObject onAction(TYRuntime runtime, TYStackTrace stackTrace, TYObject thisObj, TYObject... params) {
                
                TYModule module = ((TYModuleObject) thisObj).getInternalModule().getParentModule();
                
                if (module != null) {
                    
                    return new TYModuleObject(module);
                    
                } else {
                    
                    return TYObject.NIL;
                }
            }
        });
        TrinityNatives.registerMethod("Module", "getInnerModules", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> modules = new ArrayList<>();
            
            for (TYModule m : ((TYModuleObject) thisObj).getInternalModule().getModules()) {
                
                modules.add(new TYModuleObject(m));
            }
            
            return new TYArray(modules);
        });
        TrinityNatives.registerMethod("Module", "getInnerModule", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYModule m = ((TYModuleObject) thisObj).getInternalModule().getModule(((TYString) runtime.getVariable("name")).getInternalString());
            
            if (m != null) {
                
                return new TYModuleObject(m);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Module", "getInnerClasses", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            List<TYObject> classes = new ArrayList<>();
            
            for (TYClass c : ((TYModuleObject) thisObj).getInternalModule().getClasses()) {
                
                classes.add(new TYClassObject(c));
            }
            
            return new TYArray(classes);
        });
        TrinityNatives.registerMethod("Module", "getInnerClass", false, new String[]{"name"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYClass c = ((TYModuleObject) thisObj).getInternalModule().getClass(((TYString) runtime.getVariable("name")).getInternalString());
            
            if (c != null) {
                
                return new TYClassObject(c);
                
            } else {
                
                return TYObject.NIL;
            }
        });
        TrinityNatives.registerMethod("Module", "get", true, new String[]{"name"}, null, null, new ProcedureAction() {
            
            @Override
            public TYObject onAction(TYRuntime runtime, TYStackTrace stackTrace, TYObject thisObj, TYObject... params) {
                
                String name = ((TYString) runtime.getVariable("name")).getInternalString();
                
                if (ModuleRegistry.moduleExists(name)) {
                    
                    return new TYModuleObject(ModuleRegistry.getModule(name));
                    
                } else {
                    
                    return TYObject.NIL;
                }
            }
        });
    }
}
