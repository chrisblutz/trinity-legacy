package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.interpreter.errors.TrinityErrorException;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TrinityStack;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.plugins.api.Events;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class TryInstructionSet extends ChainedInstructionSet {
    
    private ProcedureAction action;
    private CatchInstructionSet catchSet = null;
    private FinallyInstructionSet finallySet = null;
    
    public TryInstructionSet(ProcedureAction action, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
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
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        int stackDepth = TrinityStack.size();
        try {
            
            if (getAction() != null) {
                
                result = getAction().onAction(newRuntime, null, TYObject.NONE);
            }
            
        } catch (TrinityErrorException e) {
            
            // Remove stack trace elements from the top of the stack.
            // These are normally removed after an instruction set completes,
            // but since this instruction set exited with an error, its
            // corresponding stack elements were not removed.
            TrinityStack.popToSize(stackDepth);
            
            PluginLoader.triggerEvent(Events.ERROR_CAUGHT, e.getErrorObject(), getFileName(), getLineNumber());
            
            CatchInstructionSet catchSet = getCatchSet();
            
            if (catchSet != null) {
                
                catchSet.setErrorObject(e.getErrorObject());
                result = catchSet.evaluate(TYObject.NONE, runtime);
                
            } else {
                
                Errors.throwError("Trinity.Errors.ParseError", "All 'try' blocks must be accompanied by a 'catch' block.", runtime);
            }
            
        } finally {
            
            if (getFinallySet() != null) {
                
                result = getFinallySet().evaluate(TYObject.NONE, runtime);
            }
        }
        
        newRuntime.dispose(runtime);
        
        return result;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "TryInstructionSet";
        
        if (getCatchSet() != null) {
            
            str += "\n" + indent + getCatchSet().toString(indent + "\t");
        }
        
        if (getFinallySet() != null) {
            
            str += "\n" + indent + getFinallySet().toString(indent + "\t");
        }
        
        return str;
    }
}
