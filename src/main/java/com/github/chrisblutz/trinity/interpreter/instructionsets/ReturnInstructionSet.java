package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class ReturnInstructionSet extends ChainedInstructionSet {
    
    private Token branchToken;
    private ChainedInstructionSet expression;
    private ProcedureAction action;
    
    public ReturnInstructionSet(Token branchToken, ChainedInstructionSet expression, String fileName, File fullFile, int lineNumber) {
        
        super(new ObjectEvaluator[0], fileName, fullFile, lineNumber);
        
        this.branchToken = branchToken;
        this.expression = expression;
    }
    
    public Token getBranchToken() {
        
        return branchToken;
    }
    
    public ChainedInstructionSet getExpression() {
        
        return expression;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        TYObject result = TYObject.NIL;
        
        if (getExpression() != null) {
            
            getExpression().evaluate(TYObject.NONE, runtime, stackTrace);
        }
        
        runtime.setReturning(true);
        runtime.setReturnObject(result);
        
        return result;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "ReturnInstructionSet [Branch Token: " + getBranchToken() + "]";
        
        if (getExpression() != null) {
            
            str += "\n" + indent + getExpression().toString(indent + "\t");
        }
        
        return str;
    }
}
