package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class TYProcedure {
    
    private ProcedureAction procedureAction;
    
    public TYProcedure(ProcedureAction procedureAction) {
        
        this.procedureAction = procedureAction;
    }
    
    public ProcedureAction getProcedureAction() {
        
        return procedureAction;
    }
    
    public TYObject call(TYRuntime runtime, TYStackTrace stackTrace, TYObject thisObj, TYObject... params) {
        
        return getProcedureAction().onAction(runtime, stackTrace, thisObj, params);
    }
}
