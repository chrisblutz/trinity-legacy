package com.github.chrisblutz.trinity.lang.errors.stacktrace;

/**
 * @author Christopher Lutz
 */
public class StackElement {
    
    private String errorClass, method, file;
    private int line;
    
    public StackElement(String errorClass, String method, String file, int line) {
        
        this.errorClass = errorClass;
        this.method = method;
        this.file = file;
        this.line = line;
    }
    
    public String getErrorClass() {
        
        return errorClass;
    }
    
    public String getMethod() {
        
        return method;
    }
    
    public String getFile() {
        
        return file;
    }
    
    public int getLine() {
        
        return line;
    }
    
    @Override
    public String toString() {
        
        if (getFile() != null) {
            
            String str = getErrorClass() + "." + getMethod() + " in file '" + getFile() + "'";
            
            if (getLine() > 0) {
                
                str += " at line " + getLine();
            }
            
            return str;
            
        } else if (getErrorClass() != null) {
            
            return getErrorClass() + "." + getMethod() + " (native)";
            
        } else {
            
            return "native method '" + getMethod() + "'";
        }
    }
}
