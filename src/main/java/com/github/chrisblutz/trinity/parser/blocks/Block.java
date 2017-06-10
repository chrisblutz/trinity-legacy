package com.github.chrisblutz.trinity.parser.blocks;

import java.io.File;
import java.util.ArrayList;


/**
 * @author Christopher Lutz
 */
public class Block extends ArrayList<BlockItem> implements BlockItem {
    
    private String fileName;
    private File fullFile;
    private int spaces;
    
    private String leadingComment;
    
    public Block(String fileName, File fullFile, int spaces) {
        
        this.fileName = fileName;
        this.fullFile = fullFile;
        this.spaces = spaces;
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public File getFullFile() {
        
        return fullFile;
    }
    
    public int getSpaces() {
        
        return spaces;
    }
    
    public String getLeadingComment() {
        
        return leadingComment;
    }
    
    public void setLeadingComment(String leadingComment) {
    
        this.leadingComment = leadingComment;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "Block (" + getFileName() + ") [Spaces: " + getSpaces() + ", Lines: " + countLines() + "]");
        
        for (BlockItem item : this) {
            
            str.append("\n").append(indent).append(item.toString(indent + "\t"));
        }
        
        return str.toString();
    }
    
    @Override
    public int countLines() {
        
        int lines = 0;
        
        for (BlockItem item : this) {
            
            lines += item.countLines();
        }
        
        return lines;
    }
}
