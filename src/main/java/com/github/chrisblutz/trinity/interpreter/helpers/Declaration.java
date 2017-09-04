package com.github.chrisblutz.trinity.interpreter.helpers;

import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;


/**
 * @author Christopher Lutz
 */
public interface Declaration {
    
    void define(Line line, Block nextBlock, InterpretEnvironment env, Location location);
}
