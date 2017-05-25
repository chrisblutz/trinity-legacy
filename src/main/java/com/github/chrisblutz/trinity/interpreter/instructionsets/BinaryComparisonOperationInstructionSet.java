package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BinaryComparisonOperationInstructionSet extends ObjectEvaluator {
    
    private Token operator;
    private ChainedInstructionSet operand;
    
    public BinaryComparisonOperationInstructionSet(Token operator, ChainedInstructionSet operand, String fileName, File fullFile, int lineNumber) {
        
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
        
        int comparisonInt = TrinityNatives.toInt(thisObj.tyInvoke("compareTo", runtime, null, null, opObj));
        
        switch (getOperator()) {
            
            case LESS_THAN:
                
                if (comparisonInt < 0) {
                    
                    return TYBoolean.TRUE;
                    
                } else {
                    
                    return TYBoolean.FALSE;
                }
            
            case LESS_THAN_OR_EQUAL_TO:
                
                if (comparisonInt <= 0) {
                    
                    return TYBoolean.TRUE;
                    
                } else {
                    
                    return TYBoolean.FALSE;
                }
            
            case GREATER_THAN:
                
                if (comparisonInt > 0) {
                    
                    return TYBoolean.TRUE;
                    
                } else {
                    
                    return TYBoolean.FALSE;
                }
            
            case GREATER_THAN_OR_EQUAL_TO:
                
                if (comparisonInt >= 0) {
                    
                    return TYBoolean.TRUE;
                    
                } else {
                    
                    return TYBoolean.FALSE;
                }
        }
        
        return TYBoolean.FALSE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "BinaryComparisonOperationInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getOperand().toString(indent + "\t");
        
        return str;
    }
}
