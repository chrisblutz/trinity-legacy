package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeSystem {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("System.currentTimeMillis", new TYMethod("currentTimeMillis", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 0 || params[0] == TYObject.NONE) {
                
                return new TYLong(System.currentTimeMillis());
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "System.currentTimeMillis takes no argument(s).", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
    }
}
