package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BranchingIfInstructionSet extends ChainedInstructionSet {
    
    private Token branchToken;
    private ChainedInstructionSet expression;
    private ProcedureAction action;
    private BranchingIfInstructionSet child = null;
    
    public BranchingIfInstructionSet(Token branchToken, ChainedInstructionSet expression, ProcedureAction action, String fileName, File fullFile, int lineNumber) {
        
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
    
    public BranchingIfInstructionSet getChild() {
        
        return child;
    }
    
    public void setChild(BranchingIfInstructionSet child) {
        
        this.child = child;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        if (getBranchToken() == Token.IF || getBranchToken() == Token.ELSIF) {
            
            TYBoolean expBoolean = (TYBoolean) getExpression().evaluate(TYObject.NONE, newRuntime, stackTrace);
            
            if (expBoolean.getInternalBoolean()) {
                
                if (getAction() != null) {
                    
                    result = getAction().onAction(newRuntime, stackTrace, TYObject.NONE);
                }
                
            } else if (getChild() != null) {
                
                result = getChild().evaluate(TYObject.NONE, newRuntime, stackTrace);
            }
            
        } else if (getBranchToken() == Token.ELSE) {
            
            if (getAction() != null) {
                
                result = getAction().onAction(newRuntime, stackTrace, TYObject.NONE);
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
        
        String str = indent + "BranchingIfInstructionSet [Branch Token: " + getBranchToken() + "]";
        
        if (getExpression() != null) {
            
            str += "\n" + indent + getExpression().toString(indent + "\t");
        }
        
        if (getChild() != null) {
            
            str += "\n" + indent + getChild().toString(indent);
        }
        
        return str;
    }
}
