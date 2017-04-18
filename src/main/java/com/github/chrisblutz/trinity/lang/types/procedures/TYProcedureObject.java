package com.github.chrisblutz.trinity.lang.types.procedures;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class TYProcedureObject extends TYObject {
    
    private TYProcedure internalProcedure;
    private TYRuntime procedureRuntime;
    
    public TYProcedureObject(TYProcedure internal, TYRuntime procedureRuntime) {
        
        super(ClassRegistry.getClass("Procedure"));
        
        this.internalProcedure = internal;
        this.procedureRuntime = procedureRuntime;
    }
    
    public TYProcedure getInternalProcedure() {
        
        return internalProcedure;
    }
    
    public TYRuntime getProcedureRuntime() {
        
        return procedureRuntime;
    }
}
