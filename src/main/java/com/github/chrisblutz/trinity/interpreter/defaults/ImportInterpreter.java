package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class ImportInterpreter extends DeclarationInterpreter {
    
    @Override
    public void interpret(Block block, InterpretEnvironment env) {
        
        for (BlockItem line : block) {
            
            if (line instanceof BlockLine && ((BlockLine) line).getLine().size() >= 2) {
                
                Line l = ((BlockLine) line).getLine();
                
                if (l.get(0).getToken() == Token.IMPORT && l.get(1).getToken() == Token.NON_TOKEN_STRING) {
                    
                    StringBuilder importName = new StringBuilder();
                    
                    for (int i2 = 1; i2 < l.size(); i2++) {
                        
                        if (l.get(i2).getToken() == Token.NON_TOKEN_STRING) {
                            
                            importName.append(l.get(i2).getContents());
                            
                        } else {
                            
                            importName.append(l.get(i2).getToken().getLiteral());
                        }
                    }
                    
                    TrinityInterpreter.importModule(importName.toString());
                }
            }
        }
    }
}
