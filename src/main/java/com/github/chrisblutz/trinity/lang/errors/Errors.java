package com.github.chrisblutz.trinity.lang.errors;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class Errors {
    
    public static void throwError(String errorClass, String message) {
        
        throwError(errorClass, message, new TYRuntime());
    }
    
    public static void throwError(String errorClass, String message, TYRuntime runtime) {
        
        TYObject error = TrinityNatives.newInstance(errorClass, runtime, TrinityNatives.getObjectFor(message));
        TrinityNatives.call("Trinity.Kernel", "throw", runtime, TYObject.NONE, error);
    }
    
    public static void throwError(String errorClass, String message, String filename, int line) {
        
        // Mimic toString() method of Error class
        String str = errorClass;
        
        if (message != null && !message.isEmpty()) {
            
            str += ": " + message;
        }
        
        if (filename != null && line > 0) {
            
            str += "\n\tin '" + filename + "' at line " + line;
        }
        
        System.err.println(str);
        
        Trinity.exit(1);
    }
}
