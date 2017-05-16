package com.github.chrisblutz.trinity.lang.errors;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTraceElement;


/**
 * @author Christopher Lutz
 */
public class TYError {
    
    private TYClass errorClass;
    private String message;
    private TYStackTraceElement[] stackTrace;
    
    public TYError(String errorClass, String message) {
        
        this.errorClass = ClassRegistry.getClass(errorClass);
        this.message = message;
        this.stackTrace = TYStackTrace.getStackTrace();
    }
    
    public TYClass getErrorClass() {
        
        return errorClass;
    }
    
    public String getMessage() {
        
        return message;
    }
    
    public TYStackTraceElement[] getStackTrace() {
        
        return stackTrace;
    }
    
    public void throwError() {
        
        Trinity.fail(this);
    }
    
    @Override
    public String toString() {
        
        StringBuilder str = new StringBuilder(getErrorClass().getName() + ": " + getMessage());
        
        for (TYStackTraceElement element : getStackTrace()) {
            
            str.append("\n  at ").append(element.toString());
        }
        
        return str.toString();
    }
}
