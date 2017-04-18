package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeObject {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Object.hashCode", new TYMethod("hashCode", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Object", "hashCode");
            
            return thisObj == TYObject.NIL ? new TYInt(0) : new TYInt(thisObj.hashCode());
        })));
        methods.put("Object.getClass", new TYMethod("getClass", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Object", "getClass");
            
            return new TYClassObject(thisObj.getObjectClass());
        })));
        methods.put("Object.isInstance", new TYMethod("isInstance", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Object", "isInstance");
            
            if (params.length > 0 && params[0] != TYObject.NONE && params[0] instanceof TYStaticClassObject) {
                
                TYClass tyClass = ((TYStaticClassObject) params[0]).getInternalClass();
                
                return new TYBoolean(thisObj.getObjectClass().isInstanceOf(tyClass));
                
            } else {
                
                return TYBoolean.FALSE;
            }
        })));
    }
}
