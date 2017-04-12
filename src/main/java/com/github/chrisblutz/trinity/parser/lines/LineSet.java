package com.github.chrisblutz.trinity.parser.lines;

import java.io.File;
import java.util.ArrayList;


/**
 * @author Christopher Lutz
 */
public class LineSet extends ArrayList<Line> {
    
    private String fileName;
    private File fullFile;
    
    public LineSet(String fileName, File fullFile) {
        
        this.fileName = fileName;
        this.fullFile = fullFile;
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public File getFullFile() {
    
        return fullFile;
    }
    
    @Override
    public String toString() {
        
        StringBuilder str = new StringBuilder("LineSet [Filename: " + getFileName() + "]");
        
        for (Line line : this) {
            
            str.append("\n\t").append(line.toString().replace("\t", "\t\t"));
        }
        
        return str.toString();
    }
}
