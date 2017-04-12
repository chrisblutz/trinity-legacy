package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.types.nativeutils.NativeHelper;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class ClassInterpreter extends DeclarationInterpreter {
    
    @Override
    public void interpret(Block block, InterpretEnvironment env) {
        
        for (int i = 0; i < block.size(); i++) {
            
            BlockItem line = block.get(i);
            
            if (line instanceof BlockLine && ((BlockLine) line).getLine().size() >= 2) {
                
                Line l = ((BlockLine) line).getLine();
                
                if (l.get(0).getToken() == Token.CLASS) {
                    
                    boolean nativeClass = false;
                    String extension = null;
                    int offset = 1;
                    
                    if (l.get(offset).getToken() == Token.NATIVE) {
                        
                        nativeClass = true;
                        offset++;
                    }
                    
                    String className = l.get(offset).getContents();
                    offset++;
                    
                    if (l.size() - offset > 1 && l.get(offset).getToken() == Token.CLASS_EXTENSION) {
                        
                        offset++;
                        extension = l.get(offset).getContents();
                    }
                    
                    if (!env.isEmpty()) {
                        
                        className = env.getEnvironmentString() + "." + className;
                    }
                    
                    if (!nativeClass) {
                        
                        TYClass tyClass = ClassRegistry.getClass(className);
                        if (!env.getModuleStack().isEmpty()) {
                            
                            TYModule topModule = env.getLastModule();
                            tyClass.setModule(topModule);
                        }
                        if (extension != null && ClassRegistry.classExists(extension)) {
                            
                            TYClass extClass = ClassRegistry.getClass(extension);
                            tyClass.setSuperclass(extClass);
                            
                        } else if (extension != null && !env.getModuleStack().isEmpty() && env.getLastModule().hasClass(extension)) {
                            
                            TYClass externalClass = env.getLastModule().getClass(extension);
                            tyClass.setSuperclass(externalClass);
                        }
                        InterpretEnvironment newEnv = env.append(tyClass);
                        
                        if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                            
                            Block classBlock = (Block) block.get(i + 1);
                            interpretChildren(classBlock, newEnv);
                            
                            i++;
                        }
                        
                    } else {
                        
                        NativeHelper.registerNativeClass(className);
                    }
                }
            }
        }
    }
}
