package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class IndexAssignmentInstructionSet extends ObjectEvaluator {
    
    private ChainedInstructionSet assignmentObject;
    private ChainedInstructionSet[] assignmentParams;
    private Token operator;
    private ChainedInstructionSet value;
    
    public IndexAssignmentInstructionSet(ChainedInstructionSet assignmentObject, ChainedInstructionSet[] assignmentParams, Token operator, ChainedInstructionSet value, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.assignmentObject = assignmentObject;
        this.assignmentParams = assignmentParams;
        this.operator = operator;
        this.value = value;
    }
    
    public ChainedInstructionSet getAssignmentObject() {
        
        return assignmentObject;
    }
    
    public ChainedInstructionSet[] getAssignmentParams() {
        
        return assignmentParams;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public ChainedInstructionSet getValue() {
        
        return value;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject assignmentObject = getAssignmentObject().evaluate(TYObject.NONE, runtime);
        TYObject opObj = getValue().evaluate(TYObject.NONE, runtime);
        
        TYObject[] params = new TYObject[getAssignmentParams().length];
        for (int i = 0; i < getAssignmentParams().length; i++) {
            
            params[i] = getAssignmentParams()[i].evaluate(TYObject.NONE, runtime);
        }
        
        TYObject[] paramsExtended = new TYObject[params.length + 1];
        System.arraycopy(params, 0, paramsExtended, 0, params.length);
        
        if (getOperator() == Token.ASSIGNMENT_OPERATOR) {
            
            paramsExtended[paramsExtended.length - 1] = opObj;
            assignmentObject.tyInvoke("[]=", runtime, null, null, paramsExtended);
            
        } else {
            
            TYObject current = assignmentObject.tyInvoke("[]", runtime, null, null, params);
            
            if (getOperator() == Token.NIL_ASSIGNMENT_OPERATOR && current == TYObject.NIL) {
                
                paramsExtended[paramsExtended.length - 1] = opObj;
                assignmentObject.tyInvoke("[]=", runtime, null, null, paramsExtended);
                
            } else {
                
                if (getOperator() == Token.PLUS_EQUAL) {
                    
                    opObj = current.tyInvoke("+", runtime, null, null, opObj);
                    
                } else if (getOperator() == Token.MINUS_EQUAL) {
                    
                    opObj = current.tyInvoke("-", runtime, null, null, opObj);
                    
                } else if (getOperator() == Token.MULTIPLY_EQUAL) {
                    
                    opObj = current.tyInvoke("*", runtime, null, null, opObj);
                    
                } else if (getOperator() == Token.DIVIDE_EQUAL) {
                    
                    opObj = current.tyInvoke("/", runtime, null, null, opObj);
                    
                } else if (getOperator() == Token.MODULUS_EQUAL) {
                    
                    opObj = current.tyInvoke("%", runtime, null, null, opObj);
                }
                
                paramsExtended[paramsExtended.length - 1] = opObj;
                assignmentObject.tyInvoke("[]=", runtime, null, null, paramsExtended);
            }
        }
        
        return opObj;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "IndexAssignmentInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getValue().toString(indent + "\t");
        
        return str;
    }
}
