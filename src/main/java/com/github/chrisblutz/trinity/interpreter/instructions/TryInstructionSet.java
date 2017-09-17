package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.errors.TrinityErrorException;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.plugins.PluginLoader;


/**
 * @author Christopher Lutz
 */
public class TryInstructionSet extends InstructionSet {
    
    private ProcedureAction action;
    private CatchInstructionSet catchSet = null;
    private FinallyInstructionSet finallySet = null;
    
    public TryInstructionSet(ProcedureAction action, Location location) {
        
        super(new Instruction[0], location);
        
        this.action = action;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public CatchInstructionSet getCatchSet() {
        
        return catchSet;
    }
    
    public void setCatchSet(CatchInstructionSet catchSet) {
        
        this.catchSet = catchSet;
    }
    
    public FinallyInstructionSet getFinallySet() {
        
        return finallySet;
    }
    
    public void setFinallySet(FinallyInstructionSet finallySet) {
        
        this.finallySet = finallySet;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NIL;
        
        int stackDepth = TYThread.getCurrentThread().getTrinityStack().size();
        try {
            
            if (getAction() != null) {
                
                result = getAction().onAction(newRuntime, null, TYObject.NONE);
            }
            
        } catch (TrinityErrorException e) {
            
            // Remove stack trace elements from the top of the stack.
            // These are normally removed after an instruction set completes,
            // but since this instruction set exited with an error, its
            // corresponding stack elements were not removed.
            TYThread.getCurrentThread().getTrinityStack().popToSize(stackDepth);
            
            PluginLoader.triggerOnErrorCaught(e.getErrorObject(), getLocation().getFileName(), getLocation().getFile(), getLocation().getLineNumber());
            
            if (getCatchSet() != null) {
                
                getCatchSet().setErrorObject(e.getErrorObject());
                result = getCatchSet().evaluate(TYObject.NONE, runtime);
            }
            
        } finally {
            
            if (getFinallySet() != null) {
                
                result = getFinallySet().evaluate(TYObject.NONE, runtime);
            }
        }
        
        newRuntime.dispose(runtime);
        
        return result;
    }
}
