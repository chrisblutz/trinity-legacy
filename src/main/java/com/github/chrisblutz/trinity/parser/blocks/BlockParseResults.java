package com.github.chrisblutz.trinity.parser.blocks;


/**
 * @author Christopher Lutz
 */
public class BlockParseResults {
    
    private Block block;
    private int lineNumber;
    
    public BlockParseResults(Block block, int lineNumber) {
        
        this.block = block;
        this.lineNumber = lineNumber;
    }
    
    public Block getBlock() {
        
        return block;
    }
    
    public int getLineNumber() {
        
        return lineNumber;
    }
}
