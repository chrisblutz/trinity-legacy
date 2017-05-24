package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class WhileLoopInstructionSet extends ChainedInstructionSet {
    
    private ChainedInstructionSet expression;
    private ProcedureAction action;
    
    public WhileLoopInstructionSet(ChainedInstructionSet expression, ProcedureAction action, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
        this.expression = expression;
        this.action = action;
    }
    
    public ChainedInstructionSet getExpression() {
        
        return expression;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        TYRuntime newRuntime = runtime.clone();
        
        boolean expression = TrinityNatives.toBoolean(getExpression().evaluate(TYObject.NONE, newRuntime));
        
        while (expression) {
            
            getAction().onAction(newRuntime, null, TYObject.NONE);
            
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
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        return indent + "WhileLoopInstructionSet";
    }
}
