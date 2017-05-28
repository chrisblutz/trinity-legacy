package com.github.chrisblutz.trinity.utils;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class FileUtils {
    
    public static String getExtension(File f) {
        
        if (f.getName().contains(".")) {
            
            return f.getName().substring(f.getName().lastIndexOf('.') + 1);
        }
        
        return "";
    }
    
    public static void delete(File file) {
        
        if (file.isDirectory()) {
            
            File[] files = file.listFiles();
            
            if (files != null) {
                
                for (File f : files) {
                    
                    delete(f);
                }
            }
        }
        
        file.delete();
    }
}
