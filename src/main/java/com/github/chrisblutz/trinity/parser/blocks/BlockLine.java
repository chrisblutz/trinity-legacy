package com.github.chrisblutz.trinity.parser.blocks;

import com.github.chrisblutz.trinity.parser.lines.Line;


/**
 * @author Christopher Lutz
 */
public class BlockLine implements BlockItem {
    
    private String fileName;
    private Line line;
    
    public BlockLine(String fileName, Line line) {
        
        this.fileName = fileName;
        this.line = line;
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public Line getLine() {
        
        return line;
    }
    
    @Override
    public String toString() {
        
        return "BlockLine (" + getFileName() + ") [Spaces: " + getLine().getSpaces() + ", Line Number: " + getLine().getLineNumber() + "]";
    }
    
    @Override
    public String toString(String indent) {
        
        return indent + toString();
    }
    
    @Override
    public int countLines() {
        
        return 1;
    }
}
