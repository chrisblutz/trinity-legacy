package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeClass {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Class.toString", new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (thisObj instanceof TYClassObject) {
                
                return new TYString(((TYClassObject) thisObj).getInternalClass().getName());
                
            } else if (thisObj instanceof TYStaticClassObject) {
                
                return new TYString(((TYStaticClassObject) thisObj).getInternalClass().getName());
            }
            
            return new TYString("");
        })));
        methods.put("Class.==", new TYMethod("==", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            TYClass thisClass = ((TYClassObject) thisObj).getInternalClass();
            TYClass otherClass;
            
            if (params[0] instanceof TYClassObject) {
                
                otherClass = ((TYClassObject) params[0]).getInternalClass();
                
            } else if (params[0] instanceof TYStaticClassObject) {
                
                otherClass = ((TYStaticClassObject) params[0]).getInternalClass();
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Method '==' takes a class object as a parameter.", stackTrace);
                error.throwError();
                
                otherClass = TYObject.NIL.getObjectClass();
            }
            
            return new TYBoolean(thisClass == otherClass);
        })));
    }
}
