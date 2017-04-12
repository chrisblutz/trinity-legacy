package com.github.chrisblutz.trinity.parser.lines;

import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.util.ArrayList;


/**
 * @author Christopher Lutz
 */
public class Line extends ArrayList<TokenInfo> {
    
    private int lineNumber, spaces = 0;
    
    public Line(int lineNumber) {
        
        this.lineNumber = lineNumber;
    }
    
    public int getLineNumber() {
        
        return lineNumber;
    }
    
    public int getSpaces() {
        
        return spaces;
    }
    
    public void setSpaces(int spaces) {
        
        this.spaces = spaces;
    }
    
    public TokenInfo[] toArray() {
        
        return toArray(new TokenInfo[size()]);
    }
    
    @Override
    public String toString() {
        
        StringBuilder str = new StringBuilder("Line [Line Number: " + getLineNumber() + ", Spaces: " + getSpaces() + "]");
        
        for (TokenInfo info : this) {
            
            str.append("\n\t").append(info.toString());
        }
        
        return str.toString();
    }
}
