package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeString {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("String.chars", new TYMethod("chars", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "String", "chars");
            
            return ((TYString) thisObj).getCharacterArray();
        })));
        methods.put("String.+", new TYMethod("+", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "String", "+");
            
            String thisString = ((TYString) thisObj).getInternalString();
            
            TYString returnVal;
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                String objStr = ((TYString) obj.tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString();
                
                returnVal = new TYString(thisString + objStr);
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "'+' requires two operands.", stackTrace);
                error.throwError();
                
                String objStr = ((TYString) TYObject.NIL.tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString();
                
                returnVal = new TYString(thisString + objStr);
            }
            
            return returnVal;
        })));
        methods.put("String.==", new TYMethod("==", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "String", "==");
            
            if (params.length > 0) {
                
                TYObject object = params[0];
                
                if (object instanceof TYString) {
                    
                    return new TYBoolean(((TYString) thisObj).getInternalString().contentEquals(((TYString) object).getInternalString()));
                }
            }
            
            return TYBoolean.FALSE;
        })));
    }
}
