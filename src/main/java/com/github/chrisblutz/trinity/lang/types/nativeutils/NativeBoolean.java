package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeBoolean {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Boolean.toString", new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Boolean.toString(((TYBoolean) thisObj).getInternalBoolean())))));
        methods.put("Boolean.==", new TYMethod("==", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                TYObject object = params[0];
                
                if (object instanceof TYBoolean) {
                    
                    return new TYBoolean(((TYBoolean) thisObj).getInternalBoolean() == ((TYBoolean) object).getInternalBoolean());
                }
            }
            
            return TYBoolean.FALSE;
        })));
    }
}
