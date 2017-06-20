package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.plugins.api.Events;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class ClassInterpreter extends DeclarationInterpreter {
    
    @Override
    public Token getTokenIdentifier() {
        
        return Token.CLASS;
    }
    
    @Override
    public void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile) {
        
        if (line.size() >= 2) {
            
            if (line.get(0).getToken() == Token.CLASS) {
                
                boolean nativeClass = false;
                String extension = null;
                int offset = 1;
                
                if (line.get(offset).getToken() == Token.NATIVE) {
                    
                    nativeClass = true;
                    offset++;
                }
                
                String className = line.get(offset).getContents();
                offset++;
                
                if (line.size() - offset > 1 && line.get(offset).getToken() == Token.CLASS_EXTENSION) {
                    
                    offset++;
                    extension = line.get(offset).getContents();
                }
                
                if (env.hasElements()) {
                    
                    className = env.getEnvironmentString() + "." + className;
                }
                
                if (!nativeClass) {
                    
                    TYClass tyClass = ClassRegistry.getClass(className);
                    tyClass.setLeadingComments(line.getLeadingComments());
                    if (!env.getModuleStack().isEmpty()) {
                        
                        TYModule topModule = env.getLastModule();
                        tyClass.setModule(topModule);
                    }
                    if (extension != null) {
                        
                        tyClass.setSuperclassString(extension, TrinityInterpreter.getImportedModules());
                    }
                    InterpretEnvironment newEnv = env.append(tyClass);
                    
                    if (nextBlock != null) {
                        
                        interpretChildren(nextBlock, newEnv);
                    }
                    
                    PluginLoader.triggerEvent(Events.FILE_CLASS_LOAD, fileName, tyClass);
                    
                } else {
                    
                    Errors.throwSyntaxError("Trinity.Errors.ParseError", "Native classes are not currently supported.", fileName, line.getLineNumber());
                }
            }
        }
    }
}
