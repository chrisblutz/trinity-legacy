package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidArgumentNumberError;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class NativeString {
    
    public static void register(Map<String, TYMethod> methods) {
        
        methods.put("String.toString", new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(((TYString) thisObj).getInternalString()))));
        methods.put("String.chars", new TYMethod("chars", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> ((TYString) thisObj).getCharacterArray())));
        methods.put("String.+", new TYMethod("+", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            String thisString = ((TYString) thisObj).getInternalString();
            
            TYString returnVal;
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                String objStr = ((TYString) obj.tyInvoke("toString", runtime, stackTrace)).getInternalString();
                
                returnVal = new TYString(thisString + objStr);
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "'+' requires two operands.", stackTrace);
                error.throwError();
                
                String objStr = ((TYString) TYObject.NIL.tyInvoke("toString", runtime, stackTrace)).getInternalString();
                
                returnVal = new TYString(thisString + objStr);
            }
            
            return returnVal;
        })));
        methods.put("String.==", new TYMethod("==", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
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
