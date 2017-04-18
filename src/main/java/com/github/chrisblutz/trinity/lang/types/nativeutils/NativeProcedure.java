package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.procedures.TYProcedureObject;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeProcedure {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Procedure.call", new TYMethod("call", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Procedure", "call");
            
            TYProcedureObject obj = (TYProcedureObject) thisObj;
            TYProcedure proc = obj.getInternalProcedure();
            TYRuntime newRuntime = obj.getProcedureRuntime().clone();
            
            return proc.call(newRuntime, stackTrace, null, null, TYObject.NONE, params);
        })));
    }
}
