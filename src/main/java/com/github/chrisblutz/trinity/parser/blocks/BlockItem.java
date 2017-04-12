package com.github.chrisblutz.trinity.parser.blocks;

/**
 * @author Christopher Lutz
 */
public interface BlockItem {
    
    String toString(String indent);
    
    int countLines();
}
