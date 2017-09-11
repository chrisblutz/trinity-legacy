package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.plugins.api.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class ClassRegistry {
    
    private static Map<String, TYClass> classes = new HashMap<>();
    private static List<TYClass> mainClasses = new ArrayList<>();
    
    public static TYClass getClass(String className) {
        
        if (!classes.containsKey(className)) {
            
            String shortClassName;
            
            if (className.contains(".")) {
                
                shortClassName = className.substring(className.lastIndexOf('.') + 1);
                
            } else {
                
                shortClassName = className;
            }
            
            TYClass tyClass = new TYClass(className, shortClassName);
            classes.put(className, tyClass);
            
            PluginLoader.triggerEvent(Events.CLASS_LOAD, tyClass);
        }
        
        return classes.get(className);
    }
    
    public static boolean classExists(String className) {
        
        return classes.containsKey(className);
    }
    
    public static void registerMainClass(TYClass mainClass) {
        
        mainClasses.add(mainClass);
    }
    
    public static List<TYClass> getMainClasses() {
        
        return mainClasses;
    }
    
    public static void finalizeClasses() {
        
        for (TYClass tyClass : classes.values()) {
            
            tyClass.performFinalSetup();
        }
    }
    
    public static List<TYClass> getClasses() {
        
        return new ArrayList<>(classes.values());
    }
}
