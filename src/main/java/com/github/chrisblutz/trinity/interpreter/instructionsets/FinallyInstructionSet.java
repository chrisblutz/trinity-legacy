package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class FinallyInstructionSet extends ChainedInstructionSet {
    
    private ProcedureAction action;
    
    public FinallyInstructionSet(ProcedureAction action, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
        this.action = action;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
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
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        return indent + "FinallyInstructionSet";
    }
}
