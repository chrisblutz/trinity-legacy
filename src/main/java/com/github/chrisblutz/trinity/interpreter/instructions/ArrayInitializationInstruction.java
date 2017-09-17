package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class ArrayInitializationInstruction extends Instruction {
    
    private InstructionSet[] components;
    
    public ArrayInitializationInstruction(InstructionSet[] components, Location location) {
        
        super(location);
        
        this.components = components;
    }
    
    public InstructionSet[] getComponents() {
        
        return components;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        List<TYObject> components = new ArrayList<>();
        for (InstructionSet component : getComponents()) {
            
            components.add(component.evaluate(TYObject.NONE, runtime));
        }
        
        return new TYArray(components);
    }
}
