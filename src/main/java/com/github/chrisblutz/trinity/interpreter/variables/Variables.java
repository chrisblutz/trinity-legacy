package com.github.chrisblutz.trinity.interpreter.variables;

import com.github.chrisblutz.trinity.lang.TYObject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Variables {
    
    private static Map<TYObject, Map<String, TYObject>> instanceVariables = new HashMap<>();
    private static Map<String, TYObject> globalVariables = new HashMap<>();
    
    public static Map<String, TYObject> getInstanceVariables(TYObject object) {
        
        if (!getInstanceVariables().containsKey(object)) {
            
            getInstanceVariables().put(object, new HashMap<>());
        }
        
        return getInstanceVariables().get(object);
    }
    
    public static Map<TYObject, Map<String, TYObject>> getInstanceVariables() {
        
        return instanceVariables;
    }
    
    public static TYObject getGlobalVariable(String name) {
        
        return globalVariables.getOrDefault(name, TYObject.NIL);
    }
    
    public static Map<String, TYObject> getGlobalVariables() {
        
        return globalVariables;
    }
}
