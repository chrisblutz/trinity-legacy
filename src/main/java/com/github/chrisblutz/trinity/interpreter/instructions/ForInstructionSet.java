package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class ForInstructionSet extends InstructionSet {
    
    private InstructionSet initial, expression, after;
    private ProcedureAction action;
    
    public ForInstructionSet(InstructionSet initial, InstructionSet expression, InstructionSet after, ProcedureAction action, Location location) {
        
        super(new Instruction[0], location);
        
        this.initial = initial;
        this.expression = expression;
        this.after = after;
        this.action = action;
    }
    
    public InstructionSet getInitial() {
        
        return initial;
    }
    
    public InstructionSet getExpression() {
        
        return expression;
    }
    
    public InstructionSet getAfter() {
        
        return after;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        if (getInitial() != null) {
            
            getInitial().evaluate(TYObject.NONE, newRuntime);
        }
        
        boolean expression = TrinityNatives.toBoolean(getExpression().evaluate(TYObject.NONE, newRuntime));
        
        while (expression) {
            
            if (getAction() != null) {
                
                action.onAction(newRuntime, null, TYObject.NONE);
            }
            
            if (newRuntime.isReturning()) {
                
                break;
                
            } else if (newRuntime.isBroken()) {
                
                newRuntime.setBroken(false);
                break;
            }
            
            getAfter().evaluate(TYObject.NONE, newRuntime);
            
            expression = TrinityNatives.toBoolean(getExpression().evaluate(TYObject.NONE, newRuntime));
        }
        
        newRuntime.dispose(runtime);
        
        return TYObject.NONE;
    }
}
