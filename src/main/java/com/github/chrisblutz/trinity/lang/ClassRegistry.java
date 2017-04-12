package com.github.chrisblutz.trinity.lang;

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
            
            TYClass tyClass = new TYClass(className, shortClassName, null);
            classes.put(className, tyClass);
        }
        
        return classes.get(className);
    }
    
    public static void register(String className, TYClass tyClass) {
        
        classes.put(className, tyClass);
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
}
