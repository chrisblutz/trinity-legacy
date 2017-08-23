package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BitwiseUnaryInstructionSet extends ObjectEvaluator {
    
    private Token operator;
    private ChainedInstructionSet operand;
    
    public BitwiseUnaryInstructionSet(Token operator, ChainedInstructionSet operand, String fileName, File fullFile, int lineNumber) {
        
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
            
            case BITWISE_COMPLEMENT:
                
                if (opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                    
                    long thisLong = TrinityNatives.toLong(opObj);
                    return new TYLong(~thisLong);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(opObj);
                    return new TYInt(~thisInt);
                }
        }
        
        return TYObject.NIL;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "BitwiseUnaryInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getOperand().toString(indent + "\t");
        
        return str;
    }
}
