package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class CatchInstructionSet extends ChainedInstructionSet {
    
    private ProcedureAction action;
    private String errorVariable;
    private TYObject errorObject = TYObject.NIL;
    
    public CatchInstructionSet(ProcedureAction action, String errorVariable, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
        this.action = action;
        this.errorVariable = errorVariable;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public String getErrorVariable() {
        
        return errorVariable;
    }
    
    public void setErrorObject(TYObject errorObject) {
        
        this.errorObject = errorObject;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        if (getAction() != null) {
            
            newRuntime.setVariable(getErrorVariable(), errorObject);
            
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
        
        return indent + "CatchInstructionSet [Variable: " + getErrorVariable() + "]";
    }
}
