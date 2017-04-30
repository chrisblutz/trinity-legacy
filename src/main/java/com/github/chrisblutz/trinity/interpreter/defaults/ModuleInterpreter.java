package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.lang.ModuleRegistry;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class ModuleInterpreter extends DeclarationInterpreter {
    
    @Override
    public void interpret(Block block, InterpretEnvironment env) {
        
        for (int i = 0; i < block.size(); i++) {
            
            BlockItem line = block.get(i);
            
            if (line instanceof BlockLine && ((BlockLine) line).getLine().size() >= 2) {
                
                Line l = ((BlockLine) line).getLine();
                
                if (l.get(0).getToken() == Token.MODULE && l.get(1).getToken() == Token.NON_TOKEN_STRING) {
                    
                    String moduleName = l.get(1).getContents();
                    
                    if (env.hasElements()) {
                        
                        moduleName = env.getEnvironmentString() + "." + moduleName;
                    }
                    
                    TYModule tyModule = ModuleRegistry.getModule(moduleName);
                    if (!env.getModuleStack().isEmpty()) {
                        
                        TYModule topModule = env.getLastModule();
                        tyModule.setParentModule(topModule);
                    }
                    InterpretEnvironment newEnv = env.append(tyModule);
                    
                    if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                        
                        Block moduleBlock = (Block) block.get(i + 1);
                        interpretChildren(moduleBlock, newEnv);
                        
                        i++;
                    }
                }
            }
        }
    }
}
