package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.TYModuleObject;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeModule {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Module.toString", new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(((TYModuleObject) thisObj).getInternalModule().getName()))));
    }
}
