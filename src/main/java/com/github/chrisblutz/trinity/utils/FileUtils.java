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
}
