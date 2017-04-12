package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidTypeError;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;


/**
 * @author Christopher Lutz
 */
public class TYClassClass extends TYClass {
    
    public TYClassClass() {
        
        super("Class", "Class", null);
        
        registerMethod(new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(((TYClassObject) thisObj).getInternalClass().getName()))));
        registerMethod(new TYMethod("==", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            TYClass thisClass = ((TYClassObject) thisObj).getInternalClass();
            TYClass otherClass;
            
            if (params[0] instanceof TYClassObject) {
                
                otherClass = ((TYClassObject) params[0]).getInternalClass();
                
            } else if (params[0] instanceof TYStaticClassObject) {
                
                otherClass = ((TYStaticClassObject) params[0]).getInternalClass();
                
            } else {
                
                TYError error = new TYError(new TYInvalidTypeError(), "Method '==' takes a class object as a parameter.", stackTrace);
                error.throwError();
                
                otherClass = TYObject.NIL.getObjectClass();
            }
            
            return new TYBoolean(thisClass == otherClass);
        })));
    }
}
