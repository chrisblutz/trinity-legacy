package com.github.chrisblutz.trinity.utils;

import com.github.chrisblutz.trinity.Trinity;

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
    
    private static File trinityHome = null;
    
    public static File getTrinityHome() {
        
        if (trinityHome == null) {
            
            try {
                
                trinityHome = new File(new File(Trinity.class.getProtectionDomain().getCodeSource().getLocation().toURI()), "../..");
                // Resolve '../..' out of path
                trinityHome = trinityHome.getCanonicalFile();
                
            } catch (Exception e) {
                
                e.printStackTrace();
                Trinity.exit(50);
            }
        }
        
        return trinityHome;
    }
}
