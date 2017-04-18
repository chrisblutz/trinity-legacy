package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeArray {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Array.toString", new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            StringBuilder str = new StringBuilder("[");
            
            List<TYObject> objects = ((TYArray) thisObj).getInternalList();
            
            for (int i = 0; i < objects.size(); i++) {
                
                str.append(((TYString) objects.get(i).tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString());
                
                if (i < objects.size() - 1) {
                    
                    str.append(", ");
                }
            }
            
            return new TYString(str.toString());
        })));
        methods.put("Array.length", new TYMethod("length", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYInt(((TYArray) thisObj).size()))));
        methods.put("Array.add", new TYMethod("add", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                return new TYBoolean(((TYArray) thisObj).getInternalList().add(params[0]));
                
            } else {
                
                return new TYBoolean(((TYArray) thisObj).getInternalList().add(TYObject.NIL));
            }
        })));
        methods.put("Array.remove", new TYMethod("remove", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0 && params[0] instanceof TYInt) {
                
                return ((TYArray) thisObj).getInternalList().remove(((TYInt) params[0]).getInternalInteger());
            }
            
            return TYObject.NONE;
        })));
        methods.put("Array.removeObject", new TYMethod("removeObject", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                return new TYBoolean(((TYArray) thisObj).getInternalList().remove(params[0]));
            }
            
            return TYBoolean.FALSE;
        })));
        methods.put("Array.+", new TYMethod("+", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = (TYArray) thisObj;
            List<TYObject> objects = new ArrayList<>();
            objects.addAll(thisArray.getInternalList());
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
                if (obj instanceof TYArray) {
                    
                    objects.addAll(((TYArray) obj).getInternalList());
                    
                } else {
                    
                    objects.add(obj);
                }
            }
            
            return new TYArray(objects);
        })));
        methods.put("Array.[]", new TYMethod("[]", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0 && params[0] != TYObject.NONE) {
                
                if (params[0] instanceof TYInt) {
                    
                    return ((TYArray) thisObj).getInternalList().get(((TYInt) params[0]).getInternalInteger());
                    
                } else {
                    
                    TYError error = new TYError("Trinity.Errors.InvalidTypeError", "'[]' takes an Int parameter.", stackTrace);
                    error.throwError();
                }
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "'[]' takes 1 parameter.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
    }
}
