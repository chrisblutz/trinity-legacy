package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.parser.blocks.Block;


/**
 * @author Christopher Lutz
 */
public abstract class DeclarationInterpreter {
    
    public abstract void interpret(Block block, InterpretEnvironment env);
    
    public void interpretChildren(Block block, InterpretEnvironment env) {
        
        TrinityInterpreter.interpret(block, env);
    }
}
