package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;


/**
 * @author Christopher Lutz
 */
public interface VariableLocRetriever {
    
    VariableLoc evaluate(TYObject thisObj, TYRuntime runtime);
}
