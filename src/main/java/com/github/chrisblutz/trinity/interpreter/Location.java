package com.github.chrisblutz.trinity.interpreter;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class Location {
    
    private String fileName;
    private File file;
    private int lineNumber;
    
    public Location(String fileName, File file, int lineNumber){
        
        this.fileName = fileName;
        this.file = file;
        this.lineNumber = lineNumber;
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public File getFile() {
        
        return file;
    }
    
    public int getLineNumber() {
    
        return lineNumber;
    }
}
