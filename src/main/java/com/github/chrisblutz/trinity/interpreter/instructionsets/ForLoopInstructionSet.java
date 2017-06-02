package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class ForLoopInstructionSet extends ChainedInstructionSet {
    
    private ChainedInstructionSet initial, expression, after;
    private ProcedureAction action;
    
    public ForLoopInstructionSet(ChainedInstructionSet initial, ChainedInstructionSet expression, ChainedInstructionSet after, ProcedureAction action, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
        this.initial = initial;
        this.expression = expression;
        this.after = after;
        this.action = action;
    }
    
    public ChainedInstructionSet getInitial() {
        
        return initial;
    }
    
    public ChainedInstructionSet getExpression() {
        
        return expression;
    }
    
    public ChainedInstructionSet getAfter() {
        
        return after;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYRuntime newRuntime = runtime.clone();
        
        ChainedInstructionSet initial = getInitial();
        
        if (initial != null) {
            
            initial.evaluate(TYObject.NONE, newRuntime);
        }
        
        boolean expression = TrinityNatives.toBoolean(getExpression().evaluate(TYObject.NONE, newRuntime));
        
        while (expression) {
            
            ProcedureAction action = getAction();
            
            if (action != null) {
                
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
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        return indent + "ForLoopInstructionSet";
    }
}
