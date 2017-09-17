package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class InstructionSet extends Instruction {
    
    private Instruction[] instructions;
    
    public InstructionSet(Instruction[] instructions, Location location) {
        
        super(location);
        
        this.instructions = instructions;
    }
    
    public Instruction[] getInstructions() {
        
        return instructions;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        for (Instruction instruction : getInstructions()) {
            
            thisObj = instruction.evaluate(thisObj, runtime);
        }
        
        return thisObj;
    }
}
