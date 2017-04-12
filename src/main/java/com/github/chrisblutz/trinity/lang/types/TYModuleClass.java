package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;


/**
 * @author Christopher Lutz
 */
public class TYModuleClass extends TYClass {
    
    public TYModuleClass() {
        
        super("Module", "Module", null);
        
        registerMethod(new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(((TYModuleObject) thisObj).getInternalModule().getName()))));
    }
}
