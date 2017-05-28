package com.github.chrisblutz.trinity.libraries.compiling;

/**
 * @author Christopher Lutz
 */
public class CompileException extends RuntimeException {
    
    public CompileException(String file, long line, String message) {
        
        super("Compilation error on line " + line + " in " + file + ".");
        setStackTrace(new StackTraceElement[]{});
    }
}
