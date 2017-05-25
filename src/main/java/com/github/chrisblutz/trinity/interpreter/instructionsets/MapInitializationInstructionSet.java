package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class MapInitializationInstructionSet extends ObjectEvaluator {
    
    private List<ChainedInstructionSet[]> mapComponents;
    
    public MapInitializationInstructionSet(List<ChainedInstructionSet[]> mapComponents, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.mapComponents = mapComponents;
    }
    
    public List<ChainedInstructionSet[]> getMapComponents() {
        
        return mapComponents;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        Map<TYObject, TYObject> map = new HashMap<>();
        
        for (ChainedInstructionSet[] element : getMapComponents()) {
            
            if (element.length == 2) {
                
                TYObject key = element[0].evaluate(TYObject.NONE, runtime);
                TYObject value = element[1].evaluate(TYObject.NONE, runtime);
                
                map.put(key, value);
            }
        }
        
        return new TYMap(map);
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        return indent + "MapInitializationInstructionSet []";
    }
}
