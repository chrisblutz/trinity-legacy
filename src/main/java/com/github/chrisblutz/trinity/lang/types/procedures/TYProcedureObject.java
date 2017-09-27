package com.github.chrisblutz.trinity.lang.types.procedures;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYProcedureObject extends TYObject {
    
    private TYProcedure internalProcedure;
    private TYRuntime procedureRuntime;
    private boolean broken = false;
    
    public TYProcedureObject(TYProcedure internal, TYRuntime procedureRuntime) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.PROCEDURE));
        
        this.internalProcedure = internal;
        this.procedureRuntime = procedureRuntime;
    }
    
    public TYProcedure getInternalProcedure() {
        
        return internalProcedure;
    }
    
    public TYRuntime getProcedureRuntime() {
        
        return procedureRuntime;
    }
    
    public boolean isBroken() {
    
        return broken;
    }
    
    public void setBroken(boolean broken) {
        
        this.broken = broken;
    }
}
