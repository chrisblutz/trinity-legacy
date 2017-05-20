package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class ImportInterpreter extends DeclarationInterpreter {
    
    @Override
    public Token getTokenIdentifier() {
        
        return Token.IMPORT;
    }
    
    @Override
    public void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile) {
        
        if (line.size() >= 2) {
            
            if (line.get(0).getToken() == Token.IMPORT && line.get(1).getToken() == Token.NON_TOKEN_STRING) {
                
                StringBuilder importName = new StringBuilder();
                
                for (int i2 = 1; i2 < line.size(); i2++) {
                    
                    if (line.get(i2).getToken() == Token.NON_TOKEN_STRING) {
                        
                        importName.append(line.get(i2).getContents());
                        
                    } else {
                        
                        importName.append(line.get(i2).getToken().getLiteral());
                    }
                }
                
                TrinityInterpreter.importModule(importName.toString());
            }
        }
    }
}
