package com.github.chrisblutz.trinity.lang.variables;

import com.github.chrisblutz.trinity.lang.TYObject;

import java.util.Map;
import java.util.WeakHashMap;


/**
 * @author Christopher Lutz
 */
public class VariableManager {
    
    private static Map<VariableLoc, TYObject> varMap = new WeakHashMap<>();
    
    public static TYObject getVariable(VariableLoc loc) {
        
        return varMap.getOrDefault(loc, TYObject.NIL);
    }
    
    public static void put(VariableLoc loc, TYObject object) {
        
        varMap.put(loc, object);
    }
    
    public static void clearVariable(VariableLoc loc) {
        
        varMap.remove(loc);
    }
    
    public static int size() {
        
        return varMap.size();
    }
}
