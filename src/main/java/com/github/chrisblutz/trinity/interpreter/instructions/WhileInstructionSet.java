package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class WhileInstructionSet extends InstructionSet {
    
    private InstructionSet expression;
    private ProcedureAction action;
    
    public WhileInstructionSet(InstructionSet expression, ProcedureAction action, Location location) {
        
        super(new Instruction[0], location);
        
        this.expression = expression;
        this.action = action;
    }
    
    public InstructionSet getExpression() {
        
        return expression;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
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
                
            } else {
                
                expression = TrinityNatives.toBoolean(getExpression().evaluate(TYObject.NONE, newRuntime));
            }
        }
        
        newRuntime.dispose(runtime);
        
        return TYObject.NONE;
    }
}
