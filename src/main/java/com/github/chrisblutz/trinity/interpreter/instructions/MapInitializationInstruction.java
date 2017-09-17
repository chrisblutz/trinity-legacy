package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class MapInitializationInstruction extends Instruction {
    
    private List<InstructionSet[]> components;
    
    public MapInitializationInstruction(List<InstructionSet[]> components, Location location) {
        
        super(location);
        
        this.components = components;
    }
    
    public List<InstructionSet[]> getComponents() {
        
        return components;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        Map<TYObject, TYObject> map = new HashMap<>();
        for (InstructionSet[] sets : getComponents()) {
            
            if (sets.length == 2) {
                
                TYObject key = sets[0].evaluate(TYObject.NONE, runtime);
                TYObject value = sets[1].evaluate(TYObject.NONE, runtime);
                
                map.put(key, value);
            }
        }
        
        return new TYMap(map);
    }
}
