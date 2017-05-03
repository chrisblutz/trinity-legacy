package com.github.chrisblutz.trinity.lang.errors;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;


/**
 * @author Christopher Lutz
 */
public class TYSyntaxError {
    
    private TYClass errorClass;
    private String message;
    private String fileName;
    private int lineNumber;
    
    public TYSyntaxError(String errorClass, String message, String fileName, int lineNumber) {
        
        this.errorClass = ClassRegistry.getClass(errorClass);
        this.message = message;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }
    
    public TYClass getErrorClass() {
        
        return errorClass;
    }
    
    public String getMessage() {
        
        return message;
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public int getLineNumber() {
        
        return lineNumber;
    }
    
    public void throwError() {
        
        Trinity.fail(this);
    }
    
    @Override
    public String toString() {
        
        String str = getErrorClass().getName() + ": " + getMessage();
        if (getFileName() != null) {
            
            str += "\n  in " + getFileName() + " at line " + getLineNumber();
        }
        
        return str;
    }
}
