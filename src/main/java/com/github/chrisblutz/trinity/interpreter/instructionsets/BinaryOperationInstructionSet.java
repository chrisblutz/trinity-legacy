package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
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
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject opObj = getOperand().evaluate(TYObject.NONE, runtime);
        
        switch (getOperator()) {
            
            case PLUS:
                
                if (opObj instanceof TYString) {
                    
                    TYObject thisObjStr = thisObj;
                    
                    if (!(thisObj instanceof TYString)) {
                        
                        thisObjStr = thisObj.tyInvoke("toString", runtime, null, null);
                    }
                    
                    return thisObjStr.tyInvoke("+", runtime, null, null, opObj);
                    
                } else {
                    
                    return thisObj.tyInvoke("+", runtime, null, null, opObj);
                }
            
            case MINUS:
                
                return thisObj.tyInvoke("-", runtime, null, null, opObj);
            
            case MULTIPLY:
                
                return thisObj.tyInvoke("*", runtime, null, null, opObj);
            
            case DIVIDE:
                
                return thisObj.tyInvoke("/", runtime, null, null, opObj);
            
            case MODULUS:
                
                return thisObj.tyInvoke("%", runtime, null, null, opObj);
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
