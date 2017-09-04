package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.BinaryOperator;
import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class BinaryOperatorInstruction extends Instruction {
    
    private BinaryOperator operator;
    private InstructionSet operand;
    
    public BinaryOperatorInstruction(BinaryOperator operator, InstructionSet operand, Location location) {
        
        super(location);
        
        this.operator = operator;
        this.operand = operand;
    }
    
    public BinaryOperator getOperator() {
        
        return operator;
    }
    
    public InstructionSet getOperand() {
        
        return operand;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject operand = getOperand().evaluate(TYObject.NONE, runtime);
        
        return getOperator().operate(thisObj, operand, runtime);
    }
}
