package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BinaryOperationInstructionSet extends ObjectEvaluator {
    
    private Token operator;
    private ChainedInstructionSet operand;
    
    public BinaryOperationInstructionSet(Token operator, ChainedInstructionSet operand, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.operator = operator;
        this.operand = operand;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public ChainedInstructionSet getOperand() {
        
        return operand;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        TYObject opObj = getOperand().evaluate(TYObject.NONE, runtime, stackTrace);
        
        switch (getOperator()) {
            
            case PLUS:
                
                return thisObj.tyInvoke("+", runtime, stackTrace, opObj);
            
            case MINUS:
                
                return thisObj.tyInvoke("-", runtime, stackTrace, opObj);
            
            case MULTIPLY:
                
                return thisObj.tyInvoke("*", runtime, stackTrace, opObj);
            
            case DIVIDE:
                
                return thisObj.tyInvoke("/", runtime, stackTrace, opObj);
            
            case MODULUS:
                
                return thisObj.tyInvoke("%", runtime, stackTrace, opObj);
        }
        
        return TYObject.NONE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "BinaryOperationInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getOperand().toString(indent + "\t");
        
        return str;
    }
}
