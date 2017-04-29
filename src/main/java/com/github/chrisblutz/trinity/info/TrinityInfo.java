package com.github.chrisblutz.trinity.info;

import com.github.chrisblutz.trinity.Trinity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * @author Christopher Lutz
 */
public class TrinityInfo {
    
    private static Properties properties = new Properties();
    
    public static void loadInfo() {
        
        InputStream stream = Trinity.class.getResourceAsStream("/trinity-interpreter.dat");
        
        if (stream != null) {
            
            try {
                
                properties.load(stream);
                
            } catch (IOException e) {
                
                // Hide errors
            }
        }
    }
    
    public static String get(String name) {
        
        return properties.getProperty(name);
    }
    
    public static String getVersionString() {
        
        return get("trinity.name") + " (v" + get("trinity.version") + ")";
    }
}
