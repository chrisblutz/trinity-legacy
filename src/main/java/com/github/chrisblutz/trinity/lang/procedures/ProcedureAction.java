package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;


/**
 * @author Christopher Lutz
 */
public interface ProcedureAction {
    
    TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params);
}
