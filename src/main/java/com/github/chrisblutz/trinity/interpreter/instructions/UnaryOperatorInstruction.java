package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.UnaryOperator;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class UnaryOperatorInstruction extends Instruction {
    
    private UnaryOperator operator;
    
    public UnaryOperatorInstruction(UnaryOperator operator, Location location) {
        
        super(location);
        
        this.operator = operator;
    }
    
    public UnaryOperator getOperator() {
        
        return operator;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        return getOperator().operate(thisObj);
    }
}
