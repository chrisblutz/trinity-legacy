package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.instructionsets.*;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;
import com.github.chrisblutz.trinity.utils.TokenUtils;

import java.io.File;
import java.util.*;


/**
 * @author Christopher Lutz
 */
public class ExpressionInterpreter {
    
    private static InterpretEnvironment environment;
    
    public static ProcedureAction interpret(Block block, InterpretEnvironment env, String errorClass, String method, boolean includeStackTrace) {
        
        environment = env;
        
        List<ChainedInstructionSet> sets = new ArrayList<>();
        
        BranchingIfInstructionSet ifSet = null;
        BranchingSwitchInstructionSet switchSet = null;
        
        for (int i = 0; i < block.size(); i++) {
            
            Line line = ((BlockLine) block.get(i)).getLine();
            
            Block nextBlock = null;
            
            if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                
                nextBlock = (Block) block.get(i + 1);
                i++;
            }
            
            ChainedInstructionSet set = interpret(errorClass, method, block.getFileName(), block.getFullFile(), line.getLineNumber(), line.toArray(new TokenInfo[line.size()]), nextBlock);
            
            if (set instanceof BranchingIfInstructionSet) {
                
                switchSet = null;
                BranchingIfInstructionSet newIfSet = ((BranchingIfInstructionSet) set);
                
                if (newIfSet.getBranchToken() != Token.IF && ifSet != null) {
                    
                    ifSet.setChild(newIfSet);
                    
                } else {
                    
                    sets.add(set);
                }
                
                ifSet = newIfSet;
                
            } else if (set instanceof BranchingSwitchInstructionSet) {
                
                ifSet = null;
                BranchingSwitchInstructionSet newSwitchSet = ((BranchingSwitchInstructionSet) set);
                
                if (newSwitchSet.getBranchToken() != Token.SWITCH && switchSet != null) {
                    
                    switchSet.setChild(newSwitchSet);
                    
                } else {
                    
                    sets.add(set);
                }
                
                switchSet = newSwitchSet;
                
            } else {
                
                ifSet = null;
                switchSet = null;
                sets.add(set);
            }
        }
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            TYObject returnObj = TYObject.NONE;
            
            for (ChainedInstructionSet set : sets) {
                
                TYStackTrace newTrace = stackTrace.clone();
                
                if (includeStackTrace) {
                    
                    newTrace.add(errorClass, method, set.getFileName(), set.getLineNumber());
                    
                } else {
                    
                    newTrace.pop();
                    newTrace.add(errorClass, method, set.getFileName(), set.getLineNumber());
                }
                
                TYObject result = set.evaluate(TYObject.NONE, runtime, newTrace);
                
                if (result != null) {
                    
                    returnObj = result;
                }
            }
            
            return returnObj;
        };
    }
    
    public static ChainedInstructionSet interpret(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        if (tokens[0].getToken() == Token.IF || tokens[0].getToken() == Token.ELSIF || tokens[0].getToken() == Token.ELSE) {
            
            List<TokenInfo> stripped = new ArrayList<>();
            stripped.addAll(Arrays.asList(tokens));
            stripped.remove(0);
            
            ChainedInstructionSet expression = null;
            
            if (tokens[0].getToken() != Token.ELSE) {
                
                expression = interpret(errorClass, method, fileName, fullFile, lineNumber, stripped.toArray(new TokenInfo[stripped.size()]), null);
            }
            
            ProcedureAction action = null;
            
            if (nextBlock != null) {
                
                action = interpret(nextBlock, environment, errorClass, method, false);
            }
            
            return new BranchingIfInstructionSet(tokens[0].getToken(), expression, action, fileName, fullFile, lineNumber);
            
        } else if (tokens[0].getToken() == Token.SWITCH || tokens[0].getToken() == Token.CASE || tokens[0].getToken() == Token.DEFAULT) {
            
            List<TokenInfo> stripped = new ArrayList<>();
            stripped.addAll(Arrays.asList(tokens));
            stripped.remove(0);
            
            ChainedInstructionSet expression = null;
            
            if (tokens[0].getToken() != Token.DEFAULT) {
                
                expression = interpret(errorClass, method, fileName, fullFile, lineNumber, stripped.toArray(new TokenInfo[stripped.size()]), null);
            }
            
            ProcedureAction action = null;
            
            if (nextBlock != null) {
                
                action = interpret(nextBlock, environment, errorClass, method, false);
            }
            
            return new BranchingSwitchInstructionSet(tokens[0].getToken(), expression, action, fileName, fullFile, lineNumber);
            
        } else if (tokens[0].getToken() == Token.WHILE) {
            
            List<TokenInfo> stripped = new ArrayList<>();
            stripped.addAll(Arrays.asList(tokens));
            stripped.remove(0);
            
            ChainedInstructionSet expression = interpret(errorClass, method, fileName, fullFile, lineNumber, stripped.toArray(new TokenInfo[stripped.size()]), null);
            
            ProcedureAction action = null;
            
            if (nextBlock != null) {
                
                action = interpret(nextBlock, environment, errorClass, method, false);
            }
            
            return new WhileLoopInstructionSet(expression, action, fileName, fullFile, lineNumber);
            
        } else if (tokens[0].getToken() == Token.FOR) {
            
            List<TokenInfo> stripped = new ArrayList<>();
            stripped.addAll(Arrays.asList(tokens));
            stripped.remove(0);
            if (stripped.size() > 0 && stripped.get(0).getToken() == Token.LEFT_PARENTHESIS) {
                
                stripped.remove(0);
            }
            
            if (stripped.size() > 0 && stripped.get(stripped.size() - 1).getToken() == Token.RIGHT_PARENTHESIS) {
                
                stripped.remove(stripped.size() - 1);
            }
            
            ChainedInstructionSet[] expressions = interpretListOfChainedInstructionSets(errorClass, method, fileName, fullFile, lineNumber, stripped.toArray(new TokenInfo[stripped.size()]), Token.SEMICOLON, nextBlock);
            
            if (expressions.length == 3) {
                
                ProcedureAction action = null;
                
                if (nextBlock != null) {
                    
                    action = interpret(nextBlock, environment, errorClass, method, false);
                }
                
                return new ForLoopInstructionSet(expressions[0], expressions[1], expressions[2], action, fileName, fullFile, lineNumber);
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.ParseError", "For loops require 3 components.", new TYStackTrace());
                error.throwError();
            }
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.ASSIGNMENT_OPERATOR, Token.NIL_ASSIGNMENT_OPERATOR, Token.PLUS, Token.PLUS_EQUAL, Token.MINUS, Token.MINUS_EQUAL,
                Token.MULTIPLY, Token.MULTIPLY_EQUAL, Token.DIVIDE, Token.DIVIDE_EQUAL, Token.MODULUS, Token.MODULUS_EQUAL, Token.EQUAL_TO, Token.NOT_EQUAL_TO, Token.GREATER_THAN, Token.GREATER_THAN_OR_EQUAL_TO,
                Token.LESS_THAN, Token.LESS_THAN_OR_EQUAL_TO, Token.NEGATIVE_OPERATOR, Token.AND, Token.OR)) {
            
            if (TokenUtils.containsOnFirstLevel(tokens, Token.ASSIGNMENT_OPERATOR, Token.NIL_ASSIGNMENT_OPERATOR, Token.PLUS_EQUAL, Token.MINUS_EQUAL, Token.MULTIPLY_EQUAL, Token.DIVIDE_EQUAL, Token.MODULUS_EQUAL)) {
                
                if (TokenUtils.containsOnFirstLevel(tokens, Token.COMMA)) {
                    
                    ChainedInstructionSet[] sets = interpretListOfChainedInstructionSets(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.COMMA, nextBlock);
                    return new ChainedInstructionSet(sets, fileName, fullFile, lineNumber);
                    
                } else {
                    
                    return interpretAssignment(errorClass, method, fileName, fullFile, lineNumber, tokens, nextBlock);
                }
                
            } else if (TokenUtils.containsOnFirstLevel(tokens, Token.AND, Token.OR)) {
                
                return interpretBinaryAndOrOperator(errorClass, method, fileName, fullFile, lineNumber, tokens, nextBlock);
                
            } else if (TokenUtils.containsOnFirstLevel(tokens, Token.LESS_THAN, Token.LESS_THAN_OR_EQUAL_TO, Token.EQUAL_TO, Token.NOT_EQUAL_TO, Token.GREATER_THAN, Token.GREATER_THAN_OR_EQUAL_TO)) {
                
                return interpretBinaryComparisonOperator(errorClass, method, fileName, fullFile, lineNumber, tokens, nextBlock);
                
            } else if (TokenUtils.containsOnFirstLevel(tokens, Token.PLUS, Token.MINUS, Token.MULTIPLY, Token.DIVIDE, Token.MODULUS) && tokens[0].getToken() != Token.MINUS) {
                
                return interpretBinaryOperatorMath(errorClass, method, fileName, fullFile, lineNumber, tokens, nextBlock);
                
            } else if (tokens[0].getToken() == Token.MINUS) {
                
                return interpretNumericUnaryNegation(errorClass, method, fileName, fullFile, lineNumber, tokens, nextBlock);
                
            } else if (TokenUtils.containsOnFirstLevel(tokens, Token.NEGATIVE_OPERATOR)) {
                
                return interpretUnaryNegation(errorClass, method, fileName, fullFile, lineNumber, tokens, nextBlock);
            }
            
        } else if (nextBlock != null) {
            
            List<String> mandatoryParams = new ArrayList<>();
            Map<String, TYObject> optParams = new HashMap<>();
            String blockParam = null;
            int end = 0;
            
            if (tokens[tokens.length - 1].getToken() == Token.VERTICAL_BAR) {
                
                List<TokenInfo> tokenList = new ArrayList<>();
                
                for (int i = tokens.length - 2; tokens[i].getToken() != Token.VERTICAL_BAR; i--) {
                    
                    tokenList.add(0, tokens[i]);
                    end++;
                }
                end++;
                
                ParameterResults results = parseVerticalBarParameters(tokenList, errorClass, method, fileName, fullFile, lineNumber);
                mandatoryParams = results.getMandatoryParameters();
                optParams = results.getOptionalParameters();
                blockParam = results.getBlockParam();
            }
            
            ProcedureAction action = interpret(nextBlock, environment, errorClass, method, true);
            
            TYProcedure procedure = new TYProcedure(action, mandatoryParams, optParams, blockParam);
            
            TokenInfo[] newTokens = Arrays.copyOf(tokens, tokens.length - end);
            
            ChainedInstructionSet instructionSet = interpretForChainedInstructionSet(errorClass, method, fileName, fullFile, lineNumber, newTokens, nextBlock);
            ObjectEvaluator set = instructionSet.getChildren().get(instructionSet.getChildren().size() - 1);
            set.setProcedure(procedure);
            
            return instructionSet;
            
        } else {
            
            return interpretForChainedInstructionSet(errorClass, method, fileName, fullFile, lineNumber, tokens, null);
        }
        
        return null;
    }
    
    public static ChainedInstructionSet interpretForChainedInstructionSet(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        
        TokenInfo previousNonToken = null;
        TokenInfo previous = null;
        List<TokenInfo> levelTemp = new ArrayList<>();
        boolean matchingParam = false, matchingObjArrayIndex = false, matchingArray = false, matchingArrayIndex = false;
        int level = 0;
        
        for (int i = 0; i < tokens.length; i++) {
            
            TokenInfo info = tokens[i];
            
            if (i == 0 && info.getToken() == Token.LEFT_PARENTHESIS) {
                
                level++;
                matchingParam = false;
                
            } else if (i == 0 && info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                
                level++;
                matchingArray = true;
                
            } else if (level == 0) {
                
                if ((previous != null && previous.getToken() == Token.NON_TOKEN_STRING) && info.getToken() == Token.LEFT_PARENTHESIS) {
                    
                    level++;
                    matchingParam = true;
                    
                } else if ((previous != null && previous.getToken() == Token.NON_TOKEN_STRING) && info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                    
                    level++;
                    matchingArrayIndex = true;
                    
                } else if (i != 0 && info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                    
                    level++;
                    matchingObjArrayIndex = true;
                    
                } else if (info.getToken() == Token.NON_TOKEN_STRING) {
                    
                    if (previousNonToken != null) {
                        
                        evaluators.add(new InstructionSet(new TokenInfo[]{previousNonToken}, fileName, fullFile, lineNumber));
                        previousNonToken = info;
                        
                    } else {
                        
                        previousNonToken = info;
                    }
                    
                } else if ((info.getToken() == Token.INSTANCE_VAR || info.getToken() == Token.CLASS_VAR) && i + 1 < tokens.length && tokens[i + 1].getToken() == Token.NON_TOKEN_STRING) {
                    
                    evaluators.add(new InstructionSet(new TokenInfo[]{info, tokens[i + 1]}, fileName, fullFile, lineNumber));
                    i++;
                    
                } else if (info.getToken() == Token.BREAK || info.getToken() == Token.BLOCK_CHECK || info.getToken() == Token.__FILE__ || info.getToken() == Token.__LINE__ || info.getToken() == Token.SUPER || info.getToken() == Token.NIL || info.getToken() == Token.LITERAL_STRING || info.getToken() == Token.NUMERIC_STRING || info.getToken() == Token.TRUE || info.getToken() == Token.FALSE) {
                    
                    evaluators.add(new InstructionSet(new TokenInfo[]{info}, fileName, fullFile, lineNumber));
                }
                
            } else {
                
                if (info.getToken() == Token.LEFT_PARENTHESIS || info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                    
                    level++;
                    
                } else if (info.getToken() == Token.RIGHT_PARENTHESIS || info.getToken() == Token.RIGHT_SQUARE_BRACKET) {
                    
                    level--;
                }
                
                levelTemp.add(info);
                
                if (level == 0) {
                    
                    if (matchingParam) {
                        
                        matchingParam = false;
                        
                        List<TokenInfo> temp = new ArrayList<>();
                        temp.addAll(levelTemp);
                        if (levelTemp.get(levelTemp.size() - 1).getToken() == Token.RIGHT_PARENTHESIS) {
                            
                            temp.remove(temp.size() - 1);
                        }
                        levelTemp.clear();
                        
                        ChainedInstructionSet[] params = interpretListOfChainedInstructionSets(errorClass, method, fileName, fullFile, lineNumber, temp.toArray(new TokenInfo[temp.size()]), Token.COMMA, nextBlock);
                        
                        TokenInfo[] tokenArr = new TokenInfo[0];
                        if (previousNonToken != null) {
                            
                            tokenArr = new TokenInfo[]{previousNonToken};
                        }
                        previousNonToken = null;
                        
                        InstructionSet methodSet = new InstructionSet(tokenArr, fileName, fullFile, lineNumber);
                        for (ChainedInstructionSet param : params) {
                            
                            methodSet.addChild(param);
                        }
                        evaluators.add(methodSet);
                        
                    } else if (matchingArray) {
                        
                        matchingArray = false;
                        
                        List<TokenInfo> temp = new ArrayList<>();
                        temp.addAll(levelTemp);
                        if (levelTemp.get(levelTemp.size() - 1).getToken() == Token.RIGHT_SQUARE_BRACKET) {
                            
                            temp.remove(temp.size() - 1);
                        }
                        levelTemp.clear();
                        
                        ChainedInstructionSet[] params = interpretListOfChainedInstructionSets(errorClass, method, fileName, fullFile, lineNumber, temp.toArray(new TokenInfo[temp.size()]), Token.COMMA, nextBlock);
                        
                        ArrayInitializationInstructionSet arraySet = new ArrayInitializationInstructionSet(params, fileName, fullFile, lineNumber);
                        evaluators.add(arraySet);
                        
                    } else if (matchingArrayIndex) {
                        
                        matchingArrayIndex = false;
                        
                        List<TokenInfo> temp = new ArrayList<>();
                        temp.addAll(levelTemp);
                        if (levelTemp.get(levelTemp.size() - 1).getToken() == Token.RIGHT_SQUARE_BRACKET) {
                            
                            temp.remove(temp.size() - 1);
                        }
                        levelTemp.clear();
                        
                        ChainedInstructionSet[] params = interpretListOfChainedInstructionSets(errorClass, method, fileName, fullFile, lineNumber, temp.toArray(new TokenInfo[temp.size()]), Token.COMMA, nextBlock);
                        
                        TokenInfo[] tokenArr = new TokenInfo[0];
                        if (previousNonToken != null) {
                            
                            tokenArr = new TokenInfo[]{previousNonToken};
                        }
                        previousNonToken = null;
                        
                        KeyRetrievalInstructionSet keyRetrievalSet = new KeyRetrievalInstructionSet(tokenArr, fileName, fullFile, lineNumber);
                        for (ChainedInstructionSet param : params) {
                            
                            keyRetrievalSet.addChild(param);
                        }
                        evaluators.add(keyRetrievalSet);
                        
                    } else if (matchingObjArrayIndex) {
                        
                        matchingObjArrayIndex = false;
                        
                        List<TokenInfo> temp = new ArrayList<>();
                        temp.addAll(levelTemp);
                        if (levelTemp.get(levelTemp.size() - 1).getToken() == Token.RIGHT_SQUARE_BRACKET) {
                            
                            temp.remove(temp.size() - 1);
                        }
                        levelTemp.clear();
                        
                        ChainedInstructionSet[] params = interpretListOfChainedInstructionSets(errorClass, method, fileName, fullFile, lineNumber, temp.toArray(new TokenInfo[temp.size()]), Token.COMMA, nextBlock);
                        
                        KeyRetrievalInstructionSet keyRetrievalSet = new KeyRetrievalInstructionSet(new TokenInfo[0], fileName, fullFile, lineNumber);
                        
                        for (ChainedInstructionSet param : params) {
                            
                            keyRetrievalSet.addChild(param);
                        }
                        evaluators.add(keyRetrievalSet);
                        
                    } else {
                        
                        evaluators.add(interpret(errorClass, method, fileName, fullFile, lineNumber, levelTemp.toArray(new TokenInfo[levelTemp.size()]), null));
                        levelTemp.clear();
                    }
                }
            }
            
            previous = info;
        }
        
        if (previousNonToken != null) {
            
            evaluators.add(new InstructionSet(new TokenInfo[]{previousNonToken}, fileName, fullFile, lineNumber));
        }
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    public static ChainedInstructionSet[] interpretListOfChainedInstructionSets(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Token delimiter, Block nextBlock) {
        
        List<List<TokenInfo>> infoSets = new ArrayList<>();
        List<ChainedInstructionSet> sets = new ArrayList<>();
        
        List<TokenInfo> current = new ArrayList<>();
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (level == 0 && info.getToken() == delimiter) {
                
                List<TokenInfo> tempSet = new ArrayList<>();
                tempSet.addAll(current);
                infoSets.add(tempSet);
                current.clear();
                
            } else {
                
                if (info.getToken() == Token.LEFT_PARENTHESIS || info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                    
                    level++;
                    
                } else if (info.getToken() == Token.RIGHT_PARENTHESIS || info.getToken() == Token.RIGHT_SQUARE_BRACKET) {
                    
                    level--;
                }
                
                current.add(info);
            }
        }
        
        if (!current.isEmpty()) {
            
            infoSets.add(current);
        }
        
        for (int i = 0; i < infoSets.size(); i++) {
            
            List<TokenInfo> infoList = infoSets.get(i);
            
            Block block = null;
            if (i == infoSets.size() - 1) {
                
                block = nextBlock;
            }
            
            sets.add(interpret(errorClass, method, fileName, fullFile, lineNumber, infoList.toArray(new TokenInfo[infoList.size()]), block));
        }
        
        return sets.toArray(new ChainedInstructionSet[sets.size()]);
    }
    
    private static ChainedInstructionSet[] splitByToken(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Token delimiter, Block nextBlock) {
        
        List<List<TokenInfo>> infoSets = new ArrayList<>();
        
        List<TokenInfo> current = new ArrayList<>();
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (level == 0 && info.getToken() == delimiter) {
                
                List<TokenInfo> tempSet = new ArrayList<>();
                tempSet.addAll(current);
                infoSets.add(tempSet);
                current.clear();
                
            } else {
                
                if (info.getToken() == Token.LEFT_PARENTHESIS) {
                    
                    level++;
                    
                } else if (info.getToken() == Token.RIGHT_PARENTHESIS) {
                    
                    level--;
                }
                
                current.add(info);
            }
        }
        
        if (!current.isEmpty()) {
            
            infoSets.add(current);
        }
        
        List<TokenInfo> firstExpression = new ArrayList<>();
        
        for (int i = 0; i < infoSets.size() - 1; i++) {
            
            firstExpression.addAll(infoSets.get(i));
            
            if (i < infoSets.size() - 2) {
                
                firstExpression.add(new TokenInfo(delimiter, delimiter.getLiteral()));
            }
        }
        ChainedInstructionSet first = interpret(errorClass, method, fileName, fullFile, lineNumber, firstExpression.toArray(new TokenInfo[firstExpression.size()]), null);
        
        List<TokenInfo> secondExpression = infoSets.get(infoSets.size() - 1);
        ChainedInstructionSet second = interpret(errorClass, method, fileName, fullFile, lineNumber, secondExpression.toArray(new TokenInfo[secondExpression.size()]), nextBlock);
        
        return new ChainedInstructionSet[]{first, second};
    }
    
    private static ChainedInstructionSet interpretBinaryOperatorMath(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        
        // Maintain order of operations (perform subtraction last, so parse it first, allowing all others to be interpreted before it is)
        if (TokenUtils.containsOnFirstLevel(tokens, Token.MINUS)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.MINUS, nextBlock);
            evaluators.add(components[0]);
            
            BinaryOperationInstructionSet binOpSet = new BinaryOperationInstructionSet(Token.MINUS, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.PLUS)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.PLUS, nextBlock);
            evaluators.add(components[0]);
            
            BinaryOperationInstructionSet binOpSet = new BinaryOperationInstructionSet(Token.PLUS, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.MODULUS)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.MODULUS, nextBlock);
            evaluators.add(components[0]);
            
            BinaryOperationInstructionSet binOpSet = new BinaryOperationInstructionSet(Token.MODULUS, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.DIVIDE)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.DIVIDE, nextBlock);
            evaluators.add(components[0]);
            
            BinaryOperationInstructionSet binOpSet = new BinaryOperationInstructionSet(Token.DIVIDE, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.MULTIPLY)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.MULTIPLY, nextBlock);
            evaluators.add(components[0]);
            
            BinaryOperationInstructionSet binOpSet = new BinaryOperationInstructionSet(Token.MULTIPLY, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
        }
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    private static ChainedInstructionSet interpretBinaryComparisonOperator(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        
        if (TokenUtils.containsOnFirstLevel(tokens, Token.EQUAL_TO)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.EQUAL_TO, nextBlock);
            evaluators.add(components[0]);
            
            BinaryEqualityOperationInstructionSet binOpSet = new BinaryEqualityOperationInstructionSet(Token.EQUAL_TO, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.NOT_EQUAL_TO)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.NOT_EQUAL_TO, nextBlock);
            evaluators.add(components[0]);
            
            BinaryEqualityOperationInstructionSet binOpSet = new BinaryEqualityOperationInstructionSet(Token.NOT_EQUAL_TO, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.LESS_THAN)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.LESS_THAN, nextBlock);
            evaluators.add(components[0]);
            
            BinaryComparisonOperationInstructionSet binOpSet = new BinaryComparisonOperationInstructionSet(Token.LESS_THAN, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.LESS_THAN_OR_EQUAL_TO)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.LESS_THAN_OR_EQUAL_TO, nextBlock);
            evaluators.add(components[0]);
            
            BinaryComparisonOperationInstructionSet binOpSet = new BinaryComparisonOperationInstructionSet(Token.LESS_THAN_OR_EQUAL_TO, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.GREATER_THAN)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.GREATER_THAN, nextBlock);
            evaluators.add(components[0]);
            
            BinaryComparisonOperationInstructionSet binOpSet = new BinaryComparisonOperationInstructionSet(Token.GREATER_THAN, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.GREATER_THAN_OR_EQUAL_TO)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.GREATER_THAN_OR_EQUAL_TO, nextBlock);
            evaluators.add(components[0]);
            
            BinaryComparisonOperationInstructionSet binOpSet = new BinaryComparisonOperationInstructionSet(Token.GREATER_THAN_OR_EQUAL_TO, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
        }
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    private static ChainedInstructionSet interpretBinaryAndOrOperator(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        
        if (TokenUtils.containsOnFirstLevel(tokens, Token.AND)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.AND, nextBlock);
            evaluators.add(components[0]);
            
            BinaryAndOrInstructionSet binOpSet = new BinaryAndOrInstructionSet(Token.AND, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.OR)) {
            
            ChainedInstructionSet[] components = splitByToken(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.OR, nextBlock);
            evaluators.add(components[0]);
            
            BinaryAndOrInstructionSet binOpSet = new BinaryAndOrInstructionSet(Token.OR, components[1], fileName, fullFile, lineNumber);
            evaluators.add(binOpSet);
        }
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    private static ChainedInstructionSet interpretUnaryNegation(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        
        if (tokens[0].getToken() == Token.NEGATIVE_OPERATOR) {
            
            List<TokenInfo> stripped = new ArrayList<>();
            stripped.addAll(Arrays.asList(tokens));
            stripped.remove(0);
            
            TokenInfo[] strippedArr = stripped.toArray(new TokenInfo[stripped.size()]);
            ChainedInstructionSet obj = interpret(errorClass, method, fileName, fullFile, lineNumber, strippedArr, nextBlock);
            
            UnaryNegationInstructionSet unOpSet = new UnaryNegationInstructionSet(Token.NEGATIVE_OPERATOR, obj, fileName, fullFile, lineNumber);
            evaluators.add(unOpSet);
        }
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    private static ChainedInstructionSet interpretNumericUnaryNegation(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        List<TokenInfo> stripped = new ArrayList<>();
        stripped.addAll(Arrays.asList(tokens));
        stripped.remove(0);
        
        TokenInfo[] strippedArr = stripped.toArray(new TokenInfo[stripped.size()]);
        ChainedInstructionSet obj = interpret(errorClass, method, fileName, fullFile, lineNumber, strippedArr, nextBlock);
        
        UnaryNegationInstructionSet unOpSet = new UnaryNegationInstructionSet(Token.MINUS, obj, fileName, fullFile, lineNumber);
        evaluators.add(unOpSet);
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    private static class SplitResults {
        
        private TokenInfo[] tokens;
        private ChainedInstructionSet value;
        
        public SplitResults(TokenInfo[] tokens, ChainedInstructionSet value) {
            
            this.tokens = tokens;
            this.value = value;
        }
        
        public TokenInfo[] getTokens() {
            
            return tokens;
        }
        
        public ChainedInstructionSet getValue() {
            
            return value;
        }
        
    }
    
    private static SplitResults splitIntoTokensAndValue(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Token delimiter, Block nextBlock) {
        
        List<List<TokenInfo>> tokenLists = new ArrayList<>();
        tokenLists.add(new ArrayList<>());
        tokenLists.add(new ArrayList<>());
        
        boolean hitDelimiter = false;
        
        for (TokenInfo info : tokens) {
            
            if (!hitDelimiter && info.getToken() == delimiter) {
                
                hitDelimiter = true;
                
            } else if (!hitDelimiter) {
                
                tokenLists.get(0).add(info);
                
            } else {
                
                tokenLists.get(1).add(info);
            }
        }
        
        TokenInfo[] tokenArr = tokenLists.get(0).toArray(new TokenInfo[tokenLists.get(0).size()]);
        ChainedInstructionSet value = interpret(errorClass, method, fileName, fullFile, lineNumber, tokenLists.get(1).toArray(new TokenInfo[tokenLists.get(1).size()]), nextBlock);
        
        return new SplitResults(tokenArr, value);
    }
    
    private static ChainedInstructionSet interpretAssignment(String errorClass, String method, String fileName, File fullFile, int lineNumber, TokenInfo[] tokens, Block nextBlock) {
        
        List<ObjectEvaluator> evaluators = new ArrayList<>();
        
        if (TokenUtils.containsOnFirstLevel(tokens, Token.ASSIGNMENT_OPERATOR)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.ASSIGNMENT_OPERATOR, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.ASSIGNMENT_OPERATOR, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.NIL_ASSIGNMENT_OPERATOR)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.NIL_ASSIGNMENT_OPERATOR, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.NIL_ASSIGNMENT_OPERATOR, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.PLUS_EQUAL)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.PLUS_EQUAL, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.PLUS_EQUAL, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.MINUS_EQUAL)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.MINUS_EQUAL, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.MINUS_EQUAL, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.MULTIPLY_EQUAL)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.MULTIPLY_EQUAL, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.MULTIPLY_EQUAL, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.DIVIDE_EQUAL)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.DIVIDE_EQUAL, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.DIVIDE_EQUAL, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
            
        } else if (TokenUtils.containsOnFirstLevel(tokens, Token.MODULUS_EQUAL)) {
            
            SplitResults results = splitIntoTokensAndValue(errorClass, method, fileName, fullFile, lineNumber, tokens, Token.MODULUS_EQUAL, nextBlock);
            
            AssignmentInstructionSet assignSet = new AssignmentInstructionSet(results.getTokens(), Token.MODULUS_EQUAL, results.getValue(), fileName, fullFile, lineNumber);
            evaluators.add(assignSet);
        }
        
        return new ChainedInstructionSet(evaluators.toArray(new ObjectEvaluator[evaluators.size()]), fileName, fullFile, lineNumber);
    }
    
    private static ParameterResults parseVerticalBarParameters(List<TokenInfo> tokens, String errorClass, String method, String fileName, File fullFile, int lineNumber) {
        
        List<String> mandatoryParams = new ArrayList<>();
        Map<String, TYObject> optParams = new HashMap<>();
        String blockParam = null;
        
        List<List<TokenInfo>> infoSets = new ArrayList<>();
        List<TokenInfo> paramInfo = new ArrayList<>();
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (level == 0 && info.getToken() == Token.COMMA) {
                
                List<TokenInfo> newList = new ArrayList<>();
                newList.addAll(paramInfo);
                infoSets.add(newList);
                paramInfo.clear();
                
            } else {
                
                if (info.getToken() == Token.LEFT_PARENTHESIS) {
                    
                    level++;
                    
                } else if (info.getToken() == Token.RIGHT_PARENTHESIS) {
                    
                    level--;
                }
                
                paramInfo.add(info);
            }
        }
        
        if (!paramInfo.isEmpty()) {
            
            infoSets.add(paramInfo);
        }
        
        for (List<TokenInfo> list : infoSets) {
            
            if (list.size() == 1 && list.get(0).getToken() == Token.NON_TOKEN_STRING) {
                
                mandatoryParams.add(list.get(0).getContents());
                
            } else if (list.size() > 2 && list.get(0).getToken() == Token.NON_TOKEN_STRING && list.get(1).getToken() == Token.ASSIGNMENT_OPERATOR) {
                
                List<TokenInfo> newList = new ArrayList<>();
                newList.addAll(list);
                newList.remove(0);
                newList.remove(0);
                
                ChainedInstructionSet value = ExpressionInterpreter.interpret(errorClass, method, fileName, fullFile, lineNumber, newList.toArray(new TokenInfo[newList.size()]), null);
                TYObject valueResult = TYObject.NIL;
                
                if (value != null) {
                    
                    valueResult = value.evaluate(TYObject.NONE, new TYRuntime(), new TYStackTrace());
                }
                
                optParams.put(list.get(0).getContents(), valueResult);
                
            } else if (list.size() == 2 && list.get(0).getToken() == Token.BLOCK_PREFIX && list.get(1).getToken() == Token.NON_TOKEN_STRING) {
                
                blockParam = list.get(1).getContents();
            }
        }
        
        return new ParameterResults(mandatoryParams, optParams, blockParam);
    }
}
