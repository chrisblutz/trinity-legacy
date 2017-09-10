package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.natives.NativeStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class ModuleRegistry {
    
    private static Map<String, TYModule> modules = new HashMap<>();
    
    public static TYModule getModule(String moduleName) {
        
        if (!modules.containsKey(moduleName)) {
            
            String shortModuleName;
            
            if (moduleName.contains(".")) {
                
                shortModuleName = moduleName.substring(moduleName.lastIndexOf('.') + 1);
                
            } else {
                
                shortModuleName = moduleName;
            }
            
            TYModule tyModule = new TYModule(moduleName, shortModuleName);
            modules.put(moduleName, tyModule);
    
            NativeStorage.clearAllModuleData();
        }
        
        return modules.get(moduleName);
    }
    
    public static boolean moduleExists(String moduleName) {
        
        return modules.containsKey(moduleName);
    }
    
    public static List<TYModule> getModules() {
        
        return new ArrayList<>(modules.values());
    }
}
