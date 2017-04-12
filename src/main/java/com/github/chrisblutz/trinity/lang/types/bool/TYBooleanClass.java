package com.github.chrisblutz.trinity.lang.types.bool;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;


/**
 * @author Christopher Lutz
 */
public class TYBooleanClass extends TYClass {
    
    public TYBooleanClass() {
        
        super("Boolean", "Boolean", null);
        
        registerMethod(new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Boolean.toString(((TYBoolean) thisObj).getInternalBoolean())))));
        registerMethod(new TYMethod("==", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
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
