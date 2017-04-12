package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public interface ProcedureAction {
    
    TYObject onAction(TYRuntime runtime, TYStackTrace stackTrace, TYObject thisObj, TYObject... params);
}
