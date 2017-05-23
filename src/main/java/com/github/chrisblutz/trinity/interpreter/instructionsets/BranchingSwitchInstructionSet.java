package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BranchingSwitchInstructionSet extends ChainedInstructionSet {
    
    private Token branchToken;
    private ChainedInstructionSet expression;
    private ProcedureAction action;
    private BranchingSwitchInstructionSet child = null;
    
    public BranchingSwitchInstructionSet(Token branchToken, ChainedInstructionSet expression, ProcedureAction action, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
        this.branchToken = branchToken;
        this.expression = expression;
        this.action = action;
    }
    
    public Token getBranchToken() {
        
        return branchToken;
    }
    
    public ChainedInstructionSet getExpression() {
        
        return expression;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public BranchingSwitchInstructionSet getChild() {
        
        return child;
    }
    
    public void setChild(BranchingSwitchInstructionSet child) {
        
        this.child = child;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        boolean chaining = runtime.isChainingSwitch();
        runtime.setChainingSwitch(false);
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        if (getBranchToken() == Token.SWITCH) {
            
            TYObject expression = getExpression().evaluate(TYObject.NONE, newRuntime);
            newRuntime.setSwitchObj(expression);
            
            if (getChild() != null) {
                
                result = getChild().evaluate(TYObject.NONE, newRuntime);
            }
            
        } else if (getBranchToken() == Token.CASE) {
            
            TYObject exp = getExpression().evaluate(TYObject.NONE, newRuntime);
            
            if (chaining || TrinityNatives.cast(TYBoolean.class, runtime.getSwitchObj().tyInvoke("==", runtime, null, null, exp)).getInternalBoolean()) {
                
                newRuntime.setChainingSwitch(true);
                
                if (getAction() != null) {
                    
                    result = getAction().onAction(newRuntime, null, TYObject.NONE);
                }
                
                if (!newRuntime.isBroken() && !newRuntime.isReturning() && getChild() != null) {
                    
                    result = getChild().evaluate(TYObject.NONE, newRuntime);
                }
                
            } else if (getChild() != null) {
                
                result = getChild().evaluate(TYObject.NONE, newRuntime);
            }
            
        } else if (getBranchToken() == Token.DEFAULT) {
            
            if (getAction() != null) {
                
                result = getAction().onAction(newRuntime, null, TYObject.NONE);
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
        
        String str = indent + "BranchingSwitchInstructionSet [Branch Token: " + getBranchToken() + "]";
        
        if (getExpression() != null) {
            
            str += "\n" + indent + getExpression().toString(indent + "\t");
        }
        
        if (getChild() != null) {
            
            str += "\n" + indent + getChild().toString(indent);
        }
        
        return str;
    }
}
