package com.github.chrisblutz.trinity.lang.errors.stacktrace;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYStackTrace {
    
    private static List<TYStackTraceElement> stackTrace = new ArrayList<>();
    
    static {
        
        stackTrace.add(TYStackTraceElement.getNativeInitStackTraceElement());
    }
    
    public static void add(String errorClass, String method, String file, int line) {
        
        stackTrace.add(0, new TYStackTraceElement(errorClass, method, file, line));
    }
    
    public static void pop() {
        
        stackTrace.remove(0);
    }
    
    public static TYStackTraceElement[] getStackTrace() {
        
        return stackTrace.toArray(new TYStackTraceElement[stackTrace.size()]);
    }
}
