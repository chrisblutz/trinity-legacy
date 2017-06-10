package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.lang.ModuleRegistry;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class ModuleInterpreter extends DeclarationInterpreter {
    
    @Override
    public Token getTokenIdentifier() {
        
        return Token.MODULE;
    }
    
    @Override
    public void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile) {
        
        if (line.size() >= 2) {
            
            if (line.get(0).getToken() == Token.MODULE && line.get(1).getToken() == Token.NON_TOKEN_STRING) {
                
                String moduleName = line.get(1).getContents();
                
                if (env.hasElements()) {
                    
                    moduleName = env.getEnvironmentString() + "." + moduleName;
                }
                
                TYModule tyModule = ModuleRegistry.getModule(moduleName);
                tyModule.setLeadingComments(line.getLeadingComments());
                if (!env.getModuleStack().isEmpty()) {
                    
                    TYModule topModule = env.getLastModule();
                    tyModule.setParentModule(topModule);
                }
                InterpretEnvironment newEnv = env.append(tyModule);
                
                if (nextBlock != null) {
                    
                    interpretChildren(nextBlock, newEnv);
                }
            }
        }
    }
}
