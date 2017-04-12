package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class BinaryAndOrInstructionSet extends ObjectEvaluator {
    
    private Token operator;
    private ChainedInstructionSet operand;
    
    public BinaryAndOrInstructionSet(Token operator, ChainedInstructionSet operand, String fileName, File fullFile, int lineNumber) {
        
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
        
        if (thisObj instanceof TYBoolean) {
            
            switch (getOperator()) {
                
                case AND:
                    
                    if (!((TYBoolean) thisObj).getInternalBoolean()) {
                        
                        return TYBoolean.FALSE;
                        
                    } else {
                        
                        TYObject opObj = getOperand().evaluate(TYObject.NONE, runtime, stackTrace);
                        
                        return new TYBoolean(((TYBoolean) thisObj).getInternalBoolean() && ((TYBoolean) opObj).getInternalBoolean());
                    }
                
                case OR:
                    
                    if (((TYBoolean) thisObj).getInternalBoolean()) {
                        
                        return TYBoolean.TRUE;
                        
                    } else {
                        
                        TYObject opObj = getOperand().evaluate(TYObject.NONE, runtime, stackTrace);
                        
                        return new TYBoolean(((TYBoolean) thisObj).getInternalBoolean() || ((TYBoolean) opObj).getInternalBoolean());
                    }
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
        
        String str = indent + "BinaryAndOrInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getOperand().toString(indent + "\t");
        
        return str;
    }
}
