package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.ExpressionInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.interpreter.instructionsets.ChainedInstructionSet;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;
import java.util.*;


/**
 * @author Christopher Lutz
 */
public class MethodInterpreter extends DeclarationInterpreter {
    
    @Override
    public Token getTokenIdentifier() {
        
        return Token.DEF;
    }
    
    @Override
    public void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile) {
        
        if (line.size() >= 2) {
            
            if (line.get(0).getToken() == Token.DEF) {
                
                boolean staticMethod = false, nativeMethod = false, secureMethod = false;
                String name;
                int position = 1;
                
                if (line.get(position).getToken() == Token.NATIVE) {
                    
                    nativeMethod = true;
                    position++;
                }
                
                if (line.get(position).getToken() == Token.STATIC) {
                    
                    staticMethod = true;
                    position++;
                }
                
                if (line.get(position).getToken() == Token.SECURE) {
                    
                    secureMethod = true;
                    position++;
                }
                
                TokenInfo nameInfo = line.get(position);
                if (nameInfo.getToken() == Token.NON_TOKEN_STRING) {
                    
                    name = nameInfo.getContents();
                    position++;
                    
                } else if (nameInfo.getToken() == Token.PLUS || nameInfo.getToken() == Token.MINUS || nameInfo.getToken() == Token.MULTIPLY || nameInfo.getToken() == Token.DIVIDE || nameInfo.getToken() == Token.MODULUS) {
                    
                    name = nameInfo.getContents();
                    position++;
                    
                } else if (nameInfo.getToken() == Token.LEFT_SQUARE_BRACKET && position + 1 < line.size() && line.get(position + 1).getToken() == Token.RIGHT_SQUARE_BRACKET) {
                    
                    name = "[]";
                    position += 2;
                    
                } else {
                    
                    name = nameInfo.getToken().getReadable();
                    position++;
                }
                
                TYClass containerClass = null;
                
                if (!env.getClassStack().isEmpty()) {
                    
                    containerClass = env.getClassStack().get(env.getClassStack().size() - 1);
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.ScopeError", "Methods must be declared within a class.", fileName, line.getLineNumber());
                }
                
                if (!nativeMethod) {
                    
                    List<String> mandatoryParams = new ArrayList<>();
                    Map<String, ProcedureAction> optParams = new TreeMap<>();
                    String blockParam = null;
                    
                    if (position < line.size() && line.get(position).getToken() == Token.LEFT_PARENTHESIS && line.get(line.size() - 1).getToken() == Token.RIGHT_PARENTHESIS) {
                        
                        position++;
                        TokenInfo[] tokens = Arrays.copyOfRange(line.toArray(new TokenInfo[line.size()]), position, line.size() - 1);
                        List<List<TokenInfo>> infoSets = ExpressionInterpreter.splitByTokenIntoList(tokens, Token.COMMA, fileName, line.getLineNumber());
                        
                        for (List<TokenInfo> list : infoSets) {
                            
                            if (list.size() == 1 && list.get(0).getToken() == Token.NON_TOKEN_STRING) {
                                
                                mandatoryParams.add(list.get(0).getContents());
                                
                            } else if (list.size() > 2 && list.get(0).getToken() == Token.NON_TOKEN_STRING && list.get(1).getToken() == Token.ASSIGNMENT_OPERATOR) {
                                
                                List<TokenInfo> newList = new ArrayList<>();
                                newList.addAll(list);
                                newList.remove(0);
                                newList.remove(0);
                                
                                ChainedInstructionSet value = ExpressionInterpreter.interpret(env.getLastClass().getName(), name, fileName, fullFile, line.getLineNumber(), newList.toArray(new TokenInfo[newList.size()]), null);
                                ProcedureAction action = (runtime, thisObj, params) -> value.evaluate(thisObj, runtime);
                                
                                optParams.put(list.get(0).getContents(), action);
                                
                            } else if (list.size() == 2 && list.get(0).getToken() == Token.BLOCK_PREFIX && list.get(1).getToken() == Token.NON_TOKEN_STRING) {
                                
                                blockParam = list.get(1).getContents();
                            }
                        }
                    }
                    
                    ProcedureAction action;
                    if (nextBlock != null) {
                        
                        action = ExpressionInterpreter.interpret(nextBlock, env, env.getClassStack().get(env.getClassStack().size() - 1).getName(), name, true);
                        
                    } else {
                        
                        action = (runtime, thisObj, params) -> TYObject.NONE;
                    }
                    
                    TYProcedure procedure = new TYProcedure(action, mandatoryParams, optParams, blockParam);
                    
                    TYMethod method = new TYMethod(name, staticMethod, false, secureMethod, containerClass, procedure);
                    method.setLeadingComments(line.getLeadingComments());
                    method.importModules(TrinityInterpreter.getImportedModules());
                    containerClass.registerMethod(method);
                    
                } else {
                    
                    TrinityNatives.doLoad(env.getEnvironmentString() + "." + name, secureMethod, containerClass, fileName, line.getLineNumber());
                }
            }
        }
    }
}
