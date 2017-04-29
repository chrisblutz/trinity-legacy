package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class AssignmentInstructionSet extends ObjectEvaluator {
    
    private TokenInfo[] assignmentTokens;
    private Token operator;
    private ChainedInstructionSet value;
    
    public AssignmentInstructionSet(TokenInfo[] assignmentTokens, Token operator, ChainedInstructionSet value, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.assignmentTokens = assignmentTokens;
        this.operator = operator;
        this.value = value;
    }
    
    public TokenInfo[] getAssignmentTokens() {
        
        return assignmentTokens;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public ChainedInstructionSet getValue() {
        
        return value;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        TYObject opObj = getValue().evaluate(TYObject.NONE, runtime, stackTrace);
        TYObject assignObj = TYObject.NIL;
        
        if (getAssignmentTokens().length == 1 && getAssignmentTokens()[0].getToken() == Token.NON_TOKEN_STRING) {
            
            if (runtime.hasVariable(getAssignmentTokens()[0].getContents())) {
                
                String variable = getAssignmentTokens()[0].getContents();
                assignObj = runtime.getVariable(variable);
            }
            
        } else if (!runtime.isStaticScope() && getAssignmentTokens().length == 2 && getAssignmentTokens()[0].getToken() == Token.INSTANCE_VAR && getAssignmentTokens()[1].getToken() == Token.NON_TOKEN_STRING) {
            
            assignObj = Variables.getInstanceVariables(runtime.getScope()).getOrDefault(getAssignmentTokens()[1].getContents(), TYObject.NIL);
            
        } else if (getAssignmentTokens().length == 2 && getAssignmentTokens()[0].getToken() == Token.CLASS_VAR && getAssignmentTokens()[1].getToken() == Token.NON_TOKEN_STRING) {
            
            assignObj = runtime.getTyClass().getVariables().getOrDefault(getAssignmentTokens()[1].getContents(), TYObject.NIL);
        }
        
        if (getOperator() == Token.NIL_ASSIGNMENT_OPERATOR && assignObj != TYObject.NIL) {
            
            opObj = assignObj;
            
        } else if (getOperator() == Token.PLUS_EQUAL) {
            
            opObj = assignObj.tyInvoke("+", runtime, stackTrace, null, null, opObj);
            
        } else if (getOperator() == Token.MINUS_EQUAL) {
            
            opObj = assignObj.tyInvoke("-", runtime, stackTrace, null, null, opObj);
            
        } else if (getOperator() == Token.MULTIPLY_EQUAL) {
            
            opObj = assignObj.tyInvoke("*", runtime, stackTrace, null, null, opObj);
            
        } else if (getOperator() == Token.DIVIDE_EQUAL) {
            
            opObj = assignObj.tyInvoke("/", runtime, stackTrace, null, null, opObj);
            
        } else if (getOperator() == Token.MODULUS_EQUAL) {
            
            opObj = assignObj.tyInvoke("%", runtime, stackTrace, null, null, opObj);
        }
        
        if (opObj == TYObject.NONE) {
            
            TYError error = new TYError("Trinity.Errors.AssignmentError", "Right-hand side of assignment expression must return a value.", stackTrace);
            error.throwError();
        }
        
        if (getAssignmentTokens().length == 1 && getAssignmentTokens()[0].getToken() == Token.NON_TOKEN_STRING) {
            
            String variable = getAssignmentTokens()[0].getContents();
            runtime.setVariable(variable, opObj);
            
        } else if (!runtime.isStaticScope() && getAssignmentTokens().length == 2 && getAssignmentTokens()[0].getToken() == Token.INSTANCE_VAR && getAssignmentTokens()[1].getToken() == Token.NON_TOKEN_STRING) {
            
            Variables.getInstanceVariables(runtime.getScope()).put(getAssignmentTokens()[1].getContents(), opObj);
            
        } else if (getAssignmentTokens().length == 2 && getAssignmentTokens()[0].getToken() == Token.CLASS_VAR && getAssignmentTokens()[1].getToken() == Token.NON_TOKEN_STRING) {
            
            runtime.getTyClass().getVariables().put(getAssignmentTokens()[1].getContents(), opObj);
        }
        
        return opObj;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "AssignmentInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getValue().toString(indent + "\t");
        
        return str;
    }
}
