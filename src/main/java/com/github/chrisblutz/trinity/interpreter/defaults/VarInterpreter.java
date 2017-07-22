package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.ExpressionInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.interpreter.instructionsets.ChainedInstructionSet;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TrinityStack;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class VarInterpreter extends DeclarationInterpreter {
    
    @Override
    public Token getTokenIdentifier() {
        
        return Token.VAR;
    }
    
    @Override
    public void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile) {
        
        if (line.size() >= 2) {
            
            if (line.get(0).getToken() == Token.VAR) {
                
                boolean staticVar = false, nativeVar = false;
                String name = null;
                int position = 1;
                
                if (line.get(position).getToken() == Token.NATIVE) {
                    
                    nativeVar = true;
                    position++;
                }
                
                if (line.get(position).getToken() == Token.STATIC) {
                    
                    staticVar = true;
                    position++;
                }
                
                TYClass containerClass = null;
                
                if (!env.getClassStack().isEmpty()) {
                    
                    containerClass = env.getClassStack().get(env.getClassStack().size() - 1);
                    
                } else {
                    
                    Errors.throwSyntaxError("Trinity.Errors.ScopeError", "Variables must be declared within a class.", fileName, line.getLineNumber());
                }
                
                TokenInfo[] parts = new TokenInfo[line.size() - position];
                System.arraycopy(line.toArray(new TokenInfo[line.size()]), position, parts, 0, parts.length);
                
                List<List<TokenInfo>> splitParts = ExpressionInterpreter.splitByTokenIntoList(parts, Token.COMMA, fileName, line.getLineNumber());
                
                for (List<TokenInfo> tokens : splitParts) {
                    
                    position = 0;
                    TokenInfo nameInfo = tokens.get(position);
                    if (nameInfo.getToken() == Token.NON_TOKEN_STRING) {
                        
                        name = nameInfo.getContents();
                        position++;
                    }
                    
                    ProcedureAction action;
                    if (!nativeVar) {
                        
                        if (position < tokens.size() && tokens.get(position).getToken() == Token.ASSIGNMENT_OPERATOR) {
                            
                            List<TokenInfo> assignment = new ArrayList<>();
                            for (int i = position + 1; i < tokens.size(); i++) {
                                
                                assignment.add(tokens.get(i));
                            }
                            
                            final ChainedInstructionSet set = ExpressionInterpreter.interpret(containerClass.getName(), "<nil>", fileName, fullFile, line.getLineNumber(), assignment.toArray(new TokenInfo[assignment.size()]), nextBlock);
                            final TYClass container = containerClass;
                            action = (runtime, thisObj, params) -> {
                                
                                TrinityStack.add(container.getName(), "<nil>", fileName, line.getLineNumber());
                                
                                TYObject result = set.evaluate(TYObject.NONE, runtime);
                                
                                TrinityStack.pop();
                                
                                return result;
                            };
                            
                        } else {
                            
                            action = null;
                        }
                        
                    } else {
                        
                        action = TrinityNatives.getFieldProcedureAction(containerClass.getName(), name, fileName, line.getLineNumber());
                    }
                    
                    if (staticVar) {
                        
                        containerClass.registerClassVariable(name, action, env.getScope(), false, TrinityInterpreter.getImportedModules());
                        
                    } else {
                        
                        containerClass.registerInstanceVariable(name, action, env.getScope(), false, TrinityInterpreter.getImportedModules());
                    }
                }
            }
        }
    }
}
