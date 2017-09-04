package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class FinallyInstructionSet extends InstructionSet {
    
    private ProcedureAction action;
    
    public FinallyInstructionSet(ProcedureAction action, Location location) {
        
        super(new Instruction[0], location);
        
        this.action = action;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        if (getAction() != null) {
            
            result = getAction().onAction(newRuntime, null, TYObject.NONE);
        }
        
        newRuntime.dispose(runtime);
        
        return result;
    }
}
