package com.github.chrisblutz.trinity.utils;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.cli.CLI;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * @author Christopher Lutz
 */
public class FileUtils {
    
    private static File trinityHome = null;
    
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
    
    public static File getTrinityHome() {
        
        if (trinityHome == null) {
            
            try {
                
                trinityHome = new File(new File(Trinity.class.getProtectionDomain().getCodeSource().getLocation().toURI()), "../..");
                // Resolve '../..' out of path
                trinityHome = trinityHome.getCanonicalFile();
                
            } catch (IOException e) {
                
                System.err.println("An error occurred while determining Trinity's home location.");
                
                if (CLI.isDebuggingEnabled()) {
                    
                    e.printStackTrace();
                }
                
                Trinity.exit(50);
                
            } catch (URISyntaxException e) {
                
                System.err.println("Trinity's home location returned a malformed URI.");
                
                if (CLI.isDebuggingEnabled()) {
                    
                    e.printStackTrace();
                }
                
                Trinity.exit(51);
            }
        }
        
        return trinityHome;
    }
    
    public static void checkStandardLibrary() {
        
        if (!new File(getTrinityHome(), "lib/").exists()) {
            
            System.err.println("Trinity's standard library could not be found.");
            
            Trinity.exit(80);
        }
    }
}
