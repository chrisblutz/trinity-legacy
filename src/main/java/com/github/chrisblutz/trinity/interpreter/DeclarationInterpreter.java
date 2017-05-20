package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public abstract class DeclarationInterpreter {
    
    public abstract Token getTokenIdentifier();
    
    public abstract void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile);
    
    public void interpretChildren(Block block, InterpretEnvironment env) {
        
        TrinityInterpreter.interpret(block, env);
    }
}
