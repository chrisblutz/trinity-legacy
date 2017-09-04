package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TernaryOperatorInstruction extends Instruction {
    
    private InstructionSet condition, trueValue, falseValue;
    
    public TernaryOperatorInstruction(InstructionSet condition, InstructionSet trueValue, InstructionSet falseValue, Location location) {
        
        super(location);
        
        this.condition = condition;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }
    
    public InstructionSet getCondition() {
        
        return condition;
    }
    
    public InstructionSet getTrueValue() {
        
        return trueValue;
    }
    
    public InstructionSet getFalseValue() {
        
        return falseValue;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject condition = getCondition().evaluate(TYObject.NONE, runtime);
        if (TrinityNatives.toBoolean(condition)) {
            
            return getTrueValue().evaluate(TYObject.NONE, runtime);
            
        } else {
            
            return getFalseValue().evaluate(TYObject.NONE, runtime);
        }
    }
}
