package com.github.chrisblutz.trinity.interpreter.variables;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.plugins.PluginLoader;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Variables {
    
    private static Map<String, VariableLoc> globalVariables = new HashMap<>();
    
    public static boolean hasGlobalVariable(String name) {
        
        return getGlobalVariables().containsKey(name);
    }
    
    public static VariableLoc getGlobalVariable(String name) {
        
        return globalVariables.get(name);
    }
    
    public static void setGlobalVariable(String name, TYObject object) {
        
        if (!globalVariables.containsKey(name)) {
            
            getGlobalVariables().put(name, new VariableLoc());
        }
        
        VariableManager.put(getGlobalVariables().get(name), object);
        
        PluginLoader.triggerOnGlobalVariableUpdate(name, object);
    }
    
    public static Map<String, VariableLoc> getGlobalVariables() {
        
        return globalVariables;
    }
}
