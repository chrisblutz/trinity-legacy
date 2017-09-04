package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class CatchInstructionSet extends InstructionSet {
    
    private ProcedureAction action;
    private String errorVariable;
    private TYObject errorObject = TYObject.NIL;
    private TryInstructionSet trySet;
    
    public CatchInstructionSet(ProcedureAction action, String errorVariable, Location location) {
        
        super(new Instruction[0], location);
        
        this.action = action;
        this.errorVariable = errorVariable;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public String getErrorVariable() {
        
        return errorVariable;
    }
    
    public TYObject getErrorObject() {
        
        return errorObject;
    }
    
    public void setErrorObject(TYObject errorObject) {
        
        this.errorObject = errorObject;
    }
    
    public TryInstructionSet getTrySet() {
        
        return trySet;
    }
    
    public void setTrySet(TryInstructionSet trySet) {
        
        this.trySet = trySet;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        if (getAction() != null) {
            
            newRuntime.setVariable(getErrorVariable(), getErrorObject());
            result = getAction().onAction(newRuntime, null, TYObject.NONE);
        }
        
        newRuntime.dispose(runtime);
        
        return result;
    }
}
