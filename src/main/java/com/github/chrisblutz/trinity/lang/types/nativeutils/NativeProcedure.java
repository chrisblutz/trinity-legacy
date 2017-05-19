package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.procedures.TYProcedureObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeProcedure {
    
    static void register() {
        
        TrinityNatives.registerMethod("Procedure", "initialize", false, null, null, "block", (runtime, thisObj, params) -> {
            
            if (runtime.hasVariable("block")) {
                
                return runtime.getVariable("block");
                
            } else {
                
                return new TYProcedureObject(new TYProcedure((runtime11, thisObj1, params1) -> TYObject.NIL), new TYRuntime());
            }
        });
        TrinityNatives.registerMethod("Procedure", "call", false, null, null, null, (runtime, thisObj, params) -> {
            
            TYProcedureObject obj = (TYProcedureObject) thisObj;
            TYProcedure proc = obj.getInternalProcedure();
            TYRuntime newRuntime = obj.getProcedureRuntime().clone();
            
            TYObject result = proc.call(newRuntime, null, null, TYObject.NONE, params);
            
            newRuntime.disposeVariables(obj.getProcedureRuntime());
            
            return result;
        });
    }
}
