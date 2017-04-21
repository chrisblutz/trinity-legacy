package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;


/**
 * @author Christopher Lutz
 */
public class TYNilClass extends TYClass {
    
    public TYNilClass() {
        
        super("Nil", "Nil");
        
        registerMethod(new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString("nil"))));
    }
}
