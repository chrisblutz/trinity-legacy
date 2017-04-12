package com.github.chrisblutz.trinity.lang.types.arrays;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidArgumentNumberError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidTypeError;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYArrayClass extends TYClass {
    
    public TYArrayClass() {
        
        super("Array", "Array", null);
        
        registerMethod(new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            StringBuilder str = new StringBuilder("[");
            
            List<TYObject> objects = ((TYArray) thisObj).getInternalList();
            
            for (int i = 0; i < objects.size(); i++) {
                
                str.append(((TYString) objects.get(i).tyInvoke("toString", runtime, stackTrace)).getInternalString());
                
                if (i < objects.size() - 1) {
                    
                    str.append(", ");
                }
            }
            
            return new TYString(str.toString());
        })));
        registerMethod(new TYMethod("length", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYInt(((TYArray) thisObj).size()))));
        registerMethod(new TYMethod("add", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                return new TYBoolean(((TYArray) thisObj).getInternalList().add(params[0]));
                
            } else {
                
                return new TYBoolean(((TYArray) thisObj).getInternalList().add(TYObject.NIL));
            }
        })));
        registerMethod(new TYMethod("remove", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0 && params[0] instanceof TYInt) {
                
                return ((TYArray) thisObj).getInternalList().remove(((TYInt) params[0]).getInternalInteger());
            }
            
            return TYObject.NONE;
        })));
        registerMethod(new TYMethod("removeObject", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                return new TYBoolean(((TYArray) thisObj).getInternalList().remove(params[0]));
            }
            
            return TYBoolean.FALSE;
        })));
        registerMethod(new TYMethod("+", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
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
        registerMethod(new TYMethod("[]", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0 && params[0] != TYObject.NONE) {
                
                if (params[0] instanceof TYInt) {
                    
                    return ((TYArray) thisObj).getInternalList().get(((TYInt) params[0]).getInternalInteger());
                    
                } else {
                    
                    TYError error = new TYError(new TYInvalidTypeError(), "'[]' takes an Int parameter.", stackTrace);
                    error.throwError();
                }
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "'[]' takes 1 parameter.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
    }
}
