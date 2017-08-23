package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BinaryBitwiseInstructionSet extends ObjectEvaluator {
    
    private Token operator;
    private ChainedInstructionSet operand;
    
    public BinaryBitwiseInstructionSet(Token operator, ChainedInstructionSet operand, String fileName, File fullFile, int lineNumber) {
        
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
        
        TYObject other = getOperand().evaluate(TYObject.NONE, runtime);
        
        switch (getOperator()) {
            
            case VERTICAL_BAR:
                
                if (thisObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) || other.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                    
                    long thisLong = TrinityNatives.toLong(thisObj);
                    long otherLong = TrinityNatives.toLong(other);
                    return new TYLong(thisLong | otherLong);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(thisObj);
                    int otherInt = TrinityNatives.toInt(other);
                    return new TYInt(thisInt | otherInt);
                }
            
            case BITWISE_XOR:
                
                if (thisObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) || other.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                    
                    long thisLong = TrinityNatives.toLong(thisObj);
                    long otherLong = TrinityNatives.toLong(other);
                    return new TYLong(thisLong ^ otherLong);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(thisObj);
                    int otherInt = TrinityNatives.toInt(other);
                    return new TYInt(thisInt ^ otherInt);
                }
            
            case BLOCK_PREFIX:
                
                if (thisObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) || other.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                    
                    long thisLong = TrinityNatives.toLong(thisObj);
                    long otherLong = TrinityNatives.toLong(other);
                    return new TYLong(thisLong & otherLong);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(thisObj);
                    int otherInt = TrinityNatives.toInt(other);
                    return new TYInt(thisInt & otherInt);
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
        
        String str = indent + "BinaryBitwiseInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getOperand().toString(indent + "\t");
        
        return str;
    }
}
