package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.actions.ArgumentProcedureAction;
import com.github.chrisblutz.trinity.interpreter.actions.ExpressionProcedureAction;
import com.github.chrisblutz.trinity.interpreter.helpers.KeywordExpressionHelper;
import com.github.chrisblutz.trinity.interpreter.instructions.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;
import com.github.chrisblutz.trinity.runner.Runner;

import java.util.*;


/**
 * @author Christopher Lutz
 */
public class ExpressionInterpreter {
    
    private static InterpretEnvironment environment;
    
    public static ProcedureAction interpret(Block block, InterpretEnvironment environment, String errorClass, String method, boolean includeStackTrace) {
        
        ExpressionInterpreter.environment = environment;
        
        List<InstructionSet> sets = new ArrayList<>();
        
        for (int i = 0; i < block.size(); i++) {
            
            Line line = ((BlockLine) block.get(i)).getLine();
            
            String fileName = block.getFileName();
            int lineNumber = line.getLineNumber();
            
            Runner.updateLocation(fileName, lineNumber);
            
            Block nextBlock = null;
            if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                
                nextBlock = (Block) block.get(++i);
            }
            
            if (line.size() > 0) {
                
                InstructionSet set = interpretExpression(block, line.toArray(), new Location(fileName, block.getFullFile(), lineNumber), errorClass, method, nextBlock);
                
                if (set != null) {
                    
                    sets.add(set);
                }
            }
        }
        
        return new ExpressionProcedureAction(errorClass, method, includeStackTrace, sets.toArray(new InstructionSet[sets.size()]));
    }
    
    public static InstructionSet interpretExpression(Block block, TokenInfo[] tokens, Location location, String errorClass, String method, Block nextBlock) {
        
        Token first = tokens[0].getToken();
        
        if (KeywordExpressions.isKeyword(first)) {
            
            ProcedureAction next = null;
            if (nextBlock != null) {
                
                next = interpret(nextBlock, environment, errorClass, method, false);
            }
            
            if (KeywordExpressions.checkConstraints(block, first)) {
                
                TokenInfo[] strippedTokens = new TokenInfo[tokens.length - 1];
                System.arraycopy(tokens, 1, strippedTokens, 0, strippedTokens.length);
                
                if (strippedTokens.length > 0 && strippedTokens[0].getToken() == Token.LEFT_PARENTHESIS && strippedTokens[strippedTokens.length - 1].getToken() == Token.RIGHT_PARENTHESIS && checkWrappingOnFirst(strippedTokens)) {
                    
                    TokenInfo[] temp = new TokenInfo[strippedTokens.length - 2];
                    System.arraycopy(strippedTokens, 1, temp, 0, temp.length);
                    strippedTokens = temp;
                }
                
                int comp = KeywordExpressions.getKeywordComponents(first);
                
                List<InstructionSet> components = new ArrayList<>();
                if (comp > 0) {
                    
                    if (strippedTokens.length == 0 && KeywordExpressions.isKeywordRigid(first)) {
                        
                        Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "'" + first.getLiteral() + "' statements require " + comp + " expressions, found " + components.size() + ".", location.getFileName(), location.getLineNumber());
                    }
                    
                    Token delimiter = KeywordExpressions.getKeywordDelimiter(first);
                    
                    if (delimiter != null) {
                        
                        InstructionSet[] sets = splitExpressions(strippedTokens, delimiter, location, errorClass, method, null);
                        components.addAll(Arrays.asList(sets));
                        
                    } else {
                        
                        components.add(interpretCompoundExpression(strippedTokens, location, errorClass, method, null));
                    }
                    
                    if (components.size() > comp || (components.size() < comp && KeywordExpressions.isKeywordRigid(first))) {
                        
                        Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "'" + first.getLiteral() + "' statements require " + comp + " expressions, found " + components.size() + ".", location.getFileName(), location.getLineNumber());
                    }
                    
                } else if (strippedTokens.length > 0) {
                    
                    Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "'" + first.getLiteral() + "' statements require " + comp + " expressions, found " + components.size() + ".", location.getFileName(), location.getLineNumber());
                }
                
                InstructionSet[] sets = components.toArray(new InstructionSet[components.size()]);
                
                KeywordExpressionHelper helper = KeywordExpressions.getKeywordHelper(first);
                InstructionSet thisSet = helper.interpret(sets, next, location);
                
                if (KeywordExpressions.runConstraintHelper(block, first, thisSet)) {
                    
                    KeywordExpressions.updatePrevious(block, first, thisSet);
                    
                    if (KeywordExpressions.getKeywordAddDirect(first)) {
                        
                        return thisSet;
                        
                    } else {
                        
                        return null;
                    }
                    
                } else {
                    
                    Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, KeywordExpressions.getConstraintMessage(first), location.getFileName(), location.getLineNumber());
                }
                
            } else {
                
                Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "..." + KeywordExpressions.getConstraintMessage(first), location.getFileName(), location.getLineNumber());
            }
            
        } else {
            
            TokenInfo[] expression = tokens;
            TYProcedure next = null;
            
            if (nextBlock != null) {
                
                ProcedureAction action = interpret(nextBlock, environment, errorClass, method, true);
                
                if (tokens[tokens.length - 1].getToken() == Token.VERTICAL_BAR) {
                    
                    List<TokenInfo> params = new ArrayList<>();
                    int i = tokens.length - 2;
                    for (; i >= 0; i--) {
                        
                        TokenInfo token = tokens[i];
                        if (token.getToken() == Token.VERTICAL_BAR) {
                            
                            break;
                            
                        } else {
                            
                            params.add(0, token);
                        }
                    }
                    
                    Parameters parameters = interpretParameters(params.toArray(new TokenInfo[params.size()]), location, errorClass, method);
                    
                    expression = new TokenInfo[i];
                    System.arraycopy(tokens, 0, expression, 0, expression.length);
                    
                    next = new TYProcedure(action, parameters.getMandatoryParameters(), parameters.getOptionalParameters(), parameters.getBlockParameter(), parameters.getOverflowParameter(), false);
                    
                } else {
                    
                    next = new TYProcedure(action, false);
                }
            }
            
            return interpretCompoundExpression(expression, location, errorClass, method, next);
        }
        
        return null;
    }
    
    private static boolean checkWrappingOnFirst(TokenInfo[] tokens) {
        
        int level = 0;
        for (int i = 0; i < tokens.length; i++) {
            
            Token t = tokens[i].getToken();
            
            if (t == Token.LEFT_PARENTHESIS) {
                
                level++;
                
            } else if (t == Token.RIGHT_PARENTHESIS) {
                
                level--;
            }
            
            if (level == 0 && i < tokens.length - 1) {
                
                return false;
            }
        }
        
        return true;
    }
    
    public static InstructionSet interpretCompoundExpression(TokenInfo[] tokens, Location location, String errorClass, String method, TYProcedure next) {
        
        Token[] logicalOperators = LogicalOperator.getOperators().toArray(new Token[LogicalOperator.getOperators().size()]);
        
        if (containsOnFirstLevel(tokens, AssignmentOperators.getAssignmentTokens())) {
            
            // Signifies assignment (ex. x = 10)
            if (containsOnFirstLevel(tokens, Token.COMMA)) {
                
                InstructionSet[] sets = splitExpressions(tokens, Token.COMMA, location, errorClass, method, next);
                return new InstructionSet(sets, location);
                
            } else {
                
                Token token = findOnFirstLevel(tokens, AssignmentOperators.getAssignmentTokens());
                
                List<List<TokenInfo>> tokenSets = splitTokens(tokens, token);
                TokenInfo[] assignmentTokens = tokenSets.get(0).toArray(new TokenInfo[tokenSets.get(0).size()]);
                TokenInfo[] valueTokens = tokenSets.get(1).toArray(new TokenInfo[tokenSets.get(1).size()]);
                InstructionSet value = interpretCompoundExpression(valueTokens, location, errorClass, method, next);
                
                if (assignmentTokens[assignmentTokens.length - 1].getToken() == Token.RIGHT_SQUARE_BRACKET) {
                    
                    
                    int loc = findBracketBeginning(tokens, location);
                    TokenInfo[] strippedTokens = new TokenInfo[assignmentTokens.length - loc - 2];
                    System.arraycopy(assignmentTokens, loc + 1, strippedTokens, 0, strippedTokens.length);
                    
                    InstructionSet[] indices = splitExpressions(strippedTokens, Token.COMMA, location, errorClass, method, null);
                    
                    TokenInfo[] objectTokens = new TokenInfo[loc];
                    System.arraycopy(tokens, 0, objectTokens, 0, objectTokens.length);
                    
                    InstructionSet object = interpretCompoundExpression(objectTokens, location, errorClass, method, null);
                    
                    return new InstructionSet(new Instruction[]{new IndexAssignmentInstruction(token, object, indices, value, location)}, location);
                    
                } else {
                    
                    InstructionSet assignmentObject = interpretCompoundExpression(assignmentTokens, location, errorClass, method, null);
                    
                    Instruction[] instructions = assignmentObject.getInstructions();
                    Instruction[] remainder = new Instruction[instructions.length - 1];
                    System.arraycopy(instructions, 0, remainder, 0, remainder.length);
                    Instruction end = instructions[instructions.length - 1];
                    
                    if (end instanceof InstructionSet && ((InstructionSet) end).getInstructions().length == 1) {
                        
                        end = ((InstructionSet) end).getInstructions()[0];
                    }
                    
                    InstructionSet remainderSet = new InstructionSet(remainder, assignmentObject.getLocation());
                    VariableLocRetriever retriever;
                    if (end instanceof GlobalVariableInstruction) {
                        
                        retriever = new GlobalVariableLocRetriever(((GlobalVariableInstruction) end).getName());
                        
                    } else if (end instanceof SingleTokenInstruction) {
                        
                        retriever = new SingleTokenVariableLocRetriever(((SingleTokenInstruction) end).getContents());
                        
                    } else {
                        
                        Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Invalid left-hand expression.", location.getFileName(), location.getLineNumber());
                        retriever = null;
                    }
                    
                    return new InstructionSet(new Instruction[]{new AssignmentInstruction(token, remainderSet, retriever, value, location)}, location);
                }
            }
            
        } else if (containsOnFirstLevelSequentially(tokens, Token.QUESTION_MARK, Token.COLON)) {
            
            // Signifies ternary operator usage (ex. x ? y : z)
            List<List<TokenInfo>> firstSplit = splitTokens(tokens, Token.QUESTION_MARK);
            List<TokenInfo> firstHalfList = firstSplit.get(0);
            TokenInfo[] firstHalf = firstHalfList.toArray(new TokenInfo[firstHalfList.size()]);
            
            List<TokenInfo> secondHalfList = firstSplit.get(1);
            TokenInfo[] secondHalf = secondHalfList.toArray(new TokenInfo[secondHalfList.size()]);
            
            InstructionSet first = interpretCompoundExpression(firstHalf, location, errorClass, method, null);
            InstructionSet[] secondSplit = splitByToken(secondHalf, Token.COLON, location, errorClass, method, next);
            
            return new InstructionSet(new Instruction[]{new TernaryOperatorInstruction(first, secondSplit[0], secondSplit[1], location)}, location);
            
        } else if (containsOnFirstLevel(tokens, logicalOperators)) {
            
            // Signifies binary logical operator usage (ex. x && y)
            Token token = findOnFirstLevel(tokens, logicalOperators);
            
            InstructionSet[] sets = splitByToken(tokens, token, location, errorClass, method, next);
            
            return new InstructionSet(new Instruction[]{sets[0], new LogicalOperatorInstruction(LogicalOperator.getOperator(token), sets[1], location)}, location);
            
        } else if (containsOperatorOnFirstLevel(tokens)) {
            
            // Signifies binary operator usage (ex. 1 + 2)
            Token token = findOperatorOnFirstLevel(tokens);
            
            InstructionSet[] sets = splitByTokenWithOperators(tokens, token, location, errorClass, method, next);
            
            return new InstructionSet(new Instruction[]{sets[0], new BinaryOperatorInstruction(BinaryOperator.getOperator(token), sets[1], location)}, location);
            
        } else if (containsOnFirstLevel(tokens, Token.DOUBLE_DOT, Token.TRIPLE_DOT)) {
            
            // Signifies range initialization (ex. 1..10)
            Token token = findOnFirstLevel(tokens, Token.DOUBLE_DOT, Token.TRIPLE_DOT);
            
            InstructionSet[] sets = splitByToken(tokens, token, location, errorClass, method, next);
            
            return new InstructionSet(new Instruction[]{new RangeCreationInstruction(token, sets[0], sets[1], location)}, location);
            
        } else if (tokens.length > 0 && UnaryOperator.getOperators().contains(tokens[0].getToken())) {
            
            // Signifies unary operator use (ex. -10)
            Token token = tokens[0].getToken();
            TokenInfo[] strippedTokens = new TokenInfo[tokens.length - 1];
            System.arraycopy(tokens, 1, strippedTokens, 0, strippedTokens.length);
            
            InstructionSet set = interpretCompoundExpression(strippedTokens, location, errorClass, method, next);
            
            return new InstructionSet(new Instruction[]{set, new UnaryOperatorInstruction(UnaryOperator.getOperator(token), location)}, location);
            
        } else if (containsOnFirstLevel(tokens, Token.DOT_OPERATOR)) {
            
            // Signifies expressions separated by a dot operator (ex. X.y)
            InstructionSet[] sets = splitByToken(tokens, Token.DOT_OPERATOR, location, errorClass, method, next);
            
            return new InstructionSet(new Instruction[]{sets[0], sets[1]}, location);
            
        } else if (tokens.length == 1 && Keywords.getTokens().contains(tokens[0].getToken())) {
            
            // Signifies keywords (super, nil, etc.), as well as literal and numeric strings
            return new InstructionSet(new Instruction[]{new KeywordInstruction(tokens[0], location)}, location);
            
        } else {
            
            int loc;
            if (tokens.length > 0 && tokens[tokens.length - 1].getToken() == Token.RIGHT_SQUARE_BRACKET && ((loc = findBracketBeginning(tokens, location)) > 0)) {
                
                // Signifies a [] call on an object
                TokenInfo[] strippedTokens = new TokenInfo[tokens.length - loc - 2];
                System.arraycopy(tokens, loc + 1, strippedTokens, 0, strippedTokens.length);
                
                InstructionSet[] indices = splitExpressions(strippedTokens, Token.COMMA, location, errorClass, method, null);
                
                TokenInfo[] objectTokens = new TokenInfo[loc];
                System.arraycopy(tokens, 0, objectTokens, 0, objectTokens.length);
                
                InstructionSet object = interpretCompoundExpression(objectTokens, location, errorClass, method, null);
                
                return new InstructionSet(new Instruction[]{object, new IndexAccessInstruction(indices, next, location)}, location);
                
            } else if (tokens.length > 0 && tokens[0].getToken() == Token.LEFT_PARENTHESIS) {
                
                // Signifies an expression wrapped in parentheses
                checkWrapping(tokens, Token.LEFT_PARENTHESIS, Token.RIGHT_PARENTHESIS, "Unmatched parentheses.", location);
                
                TokenInfo[] strippedTokens = new TokenInfo[tokens.length - 2];
                System.arraycopy(tokens, 1, strippedTokens, 0, strippedTokens.length);
                
                return interpretCompoundExpression(strippedTokens, location, errorClass, method, null);
                
            } else if (tokens.length > 0 && tokens[0].getToken() == Token.LEFT_SQUARE_BRACKET) {
                
                // Signifies array initialization
                checkWrapping(tokens, Token.LEFT_SQUARE_BRACKET, Token.RIGHT_SQUARE_BRACKET, "Unmatched brackets.", location);
                
                TokenInfo[] strippedTokens = new TokenInfo[tokens.length - 2];
                System.arraycopy(tokens, 1, strippedTokens, 0, strippedTokens.length);
                
                InstructionSet[] arrayComponents = splitExpressions(strippedTokens, Token.COMMA, location, errorClass, method, null);
                
                return new InstructionSet(new Instruction[]{new ArrayInitializationInstruction(arrayComponents, location)}, location);
                
            } else if (tokens.length > 0 && tokens[0].getToken() == Token.LEFT_CURLY_BRACKET) {
                
                // Signifies map initialization
                checkWrapping(tokens, Token.LEFT_CURLY_BRACKET, Token.RIGHT_CURLY_BRACKET, "Unmatched brackets.", location);
                
                TokenInfo[] strippedTokens = new TokenInfo[tokens.length - 2];
                System.arraycopy(tokens, 1, strippedTokens, 0, strippedTokens.length);
                
                List<List<TokenInfo>> tokenSets = splitTokens(strippedTokens, Token.COMMA);
                List<InstructionSet[]> mapComponents = new ArrayList<>();
                
                for (List<TokenInfo> set : tokenSets) {
                    
                    TokenInfo[] array = set.toArray(new TokenInfo[set.size()]);
                    mapComponents.add(splitByToken(array, Token.COLON, location, errorClass, method, null));
                }
                
                return new InstructionSet(new Instruction[]{new MapInitializationInstruction(mapComponents, location)}, location);
                
            } else if (tokens.length == 2 && tokens[0].getToken() == Token.GLOBAL_VAR && tokens[1].getToken() == Token.NON_TOKEN_STRING) {
                
                // Signifies global variable
                return new InstructionSet(new Instruction[]{new GlobalVariableInstruction(tokens[1].getContents(), location)}, location);
                
            } else if (tokens.length == 1 && tokens[0].getToken() == Token.NON_TOKEN_STRING) {
                
                // Signifies single-token name (class/module names, variables, etc.)
                return new InstructionSet(new Instruction[]{new SingleTokenInstruction(tokens[0].getContents(), location)}, location);
                
            } else if (tokens.length > 1 && tokens[1].getToken() == Token.LEFT_PARENTHESIS) {
                
                // Signifies a method call
                TokenInfo[] paramTokens = new TokenInfo[tokens.length - 1];
                System.arraycopy(tokens, 1, paramTokens, 0, paramTokens.length);
                
                checkWrapping(paramTokens, Token.LEFT_PARENTHESIS, Token.RIGHT_PARENTHESIS, "Unmatched parentheses.", location);
                
                TokenInfo[] strippedTokens = new TokenInfo[paramTokens.length - 2];
                System.arraycopy(paramTokens, 1, strippedTokens, 0, strippedTokens.length);
                
                InstructionSet[] params = splitExpressions(strippedTokens, Token.COMMA, location, errorClass, method, null);
                
                return new InstructionSet(new Instruction[]{new MethodCallInstruction(tokens[0].getContents(), params, next, location)}, location);
                
            } else {
                
                Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Unrecognized token.", location.getFileName(), location.getLineNumber());
            }
        }
        
        return null;
    }
    
    private static int findBracketBeginning(TokenInfo[] tokens, Location location) {
        
        int level = 0;
        for (int i = tokens.length - 1; i >= 0; i--) {
            
            TokenInfo info = tokens[i];
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
                
            }
            
            if (level == 0 && info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                
                return i;
            }
        }
        
        Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Unmatched brackets.", location.getFileName(), location.getLineNumber());
        return 0;
    }
    
    private static void checkWrapping(TokenInfo[] tokens, Token left, Token right, String errorMessage, Location location) {
        
        if (tokens[0].getToken() != left || tokens[tokens.length - 1].getToken() != right) {
            
            Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, errorMessage, location.getFileName(), location.getLineNumber());
        }
        
        // Check for imbalanced brackets
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
            }
        }
        
        if (level != 0) {
            
            Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, errorMessage, location.getFileName(), location.getLineNumber());
        }
    }
    
    private static InstructionSet[] splitExpressions(TokenInfo[] tokens, Token delimiter, Location location, String errorClass, String method, TYProcedure next) {
        
        List<List<TokenInfo>> tokenSets = splitTokens(tokens, delimiter);
        List<InstructionSet> sets = new ArrayList<>();
        
        for (int i = 0; i < tokenSets.size(); i++) {
            
            List<TokenInfo> list = tokenSets.get(i);
            TokenInfo[] listArr = list.toArray(new TokenInfo[list.size()]);
            sets.add(interpretCompoundExpression(listArr, location, errorClass, method, next));
        }
        
        return sets.toArray(new InstructionSet[sets.size()]);
    }
    
    public static List<List<TokenInfo>> splitTokens(TokenInfo[] tokens, Token delimiter) {
        
        List<List<TokenInfo>> sets = new ArrayList<>();
        
        List<TokenInfo> current = new ArrayList<>();
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (level == 0 && info.getToken() == delimiter) {
                
                List<TokenInfo> tempSet = new ArrayList<>();
                tempSet.addAll(current);
                sets.add(tempSet);
                current.clear();
                
            } else {
                
                if (isLevelUpToken(info.getToken())) {
                    
                    level++;
                    
                } else if (isLevelDownToken(info.getToken())) {
                    
                    level--;
                }
                
                current.add(info);
            }
        }
        
        if (!current.isEmpty()) {
            
            sets.add(current);
        }
        
        return sets;
    }
    
    private static List<List<TokenInfo>> splitTokensWithOperators(TokenInfo[] tokens, Token delimiter) {
        
        List<List<TokenInfo>> sets = new ArrayList<>();
        
        List<TokenInfo> current = new ArrayList<>();
        int level = 0;
        for (int i = 0; i < tokens.length; i++) {
            
            TokenInfo info = tokens[i];
            
            if (level == 0 && info.getToken() == delimiter && checkOperator(tokens, i, delimiter)) {
                
                List<TokenInfo> tempSet = new ArrayList<>();
                tempSet.addAll(current);
                sets.add(tempSet);
                current.clear();
                
            } else {
                
                if (isLevelUpToken(info.getToken())) {
                    
                    level++;
                    
                } else if (isLevelDownToken(info.getToken())) {
                    
                    level--;
                }
                
                current.add(info);
            }
        }
        
        if (!current.isEmpty()) {
            
            sets.add(current);
        }
        
        return sets;
    }
    
    private static boolean checkOperator(TokenInfo[] tokens, int i, Token token) {
        
        if (UnaryOperator.getOperators().contains(token)) {
            
            if (i == 0 || (i - 1 > 0 && BinaryOperator.getOperators().contains(tokens[i - 1].getToken()))) {
                
                return false;
            }
        }
        
        return true;
    }
    
    private static InstructionSet[] splitByToken(TokenInfo[] tokens, Token delimiter, Location location, String errorClass, String method, TYProcedure next) {
        
        List<List<TokenInfo>> tokenSets = splitTokens(tokens, delimiter);
        
        List<TokenInfo> firstExpression = new ArrayList<>();
        for (int i = 0; i < tokenSets.size() - 1; i++) {
            
            firstExpression.addAll(tokenSets.get(i));
            
            if (i < tokenSets.size() - 2) {
                
                firstExpression.add(new TokenInfo(delimiter, delimiter.getLiteral()));
            }
        }
        
        List<TokenInfo> secondExpression = tokenSets.get(tokenSets.size() - 1);
        
        InstructionSet first = interpretCompoundExpression(firstExpression.toArray(new TokenInfo[firstExpression.size()]), location, errorClass, method, null);
        InstructionSet second = interpretCompoundExpression(secondExpression.toArray(new TokenInfo[secondExpression.size()]), location, errorClass, method, next);
        
        return new InstructionSet[]{first, second};
    }
    
    private static InstructionSet[] splitByTokenWithOperators(TokenInfo[] tokens, Token delimiter, Location location, String errorClass, String method, TYProcedure next) {
        
        List<List<TokenInfo>> tokenSets = splitTokensWithOperators(tokens, delimiter);
        
        List<TokenInfo> firstExpression = new ArrayList<>();
        for (int i = 0; i < tokenSets.size() - 1; i++) {
            
            firstExpression.addAll(tokenSets.get(i));
            
            if (i < tokenSets.size() - 2) {
                
                firstExpression.add(new TokenInfo(delimiter, delimiter.getLiteral()));
            }
        }
        
        List<TokenInfo> secondExpression = tokenSets.get(tokenSets.size() - 1);
        
        InstructionSet first = interpretCompoundExpression(firstExpression.toArray(new TokenInfo[firstExpression.size()]), location, errorClass, method, null);
        InstructionSet second = interpretCompoundExpression(secondExpression.toArray(new TokenInfo[secondExpression.size()]), location, errorClass, method, next);
        
        return new InstructionSet[]{first, second};
    }
    
    public static Parameters interpretParameters(TokenInfo[] tokens, Location location, String errorClass, String method) {
        
        List<String> mandatory = new ArrayList<>();
        Map<String, ProcedureAction> optional = new TreeMap<>();
        String block = null, overflow = null;
        
        List<List<TokenInfo>> tokenSets = splitTokens(tokens, Token.COMMA);
        
        for (List<TokenInfo> list : tokenSets) {
            
            if (list.size() == 1 && list.get(0).getToken() == Token.NON_TOKEN_STRING) {
                
                mandatory.add(list.get(0).getContents());
                
            } else if (list.size() > 2 && list.get(0).getToken() == Token.NON_TOKEN_STRING && list.get(1).getToken() == Token.ASSIGNMENT_OPERATOR) {
                
                TokenInfo[] fullExp = list.toArray(new TokenInfo[list.size()]);
                TokenInfo[] optionalValue = new TokenInfo[list.size() - 2];
                System.arraycopy(fullExp, 2, optionalValue, 0, optionalValue.length);
                
                InstructionSet value = interpretCompoundExpression(optionalValue, location, errorClass, method, null);
                ProcedureAction action = new ArgumentProcedureAction(value);
                
                optional.put(list.get(0).getContents(), action);
                
            } else if (list.size() == 2 && list.get(1).getToken() == Token.NON_TOKEN_STRING) {
                
                String contents = list.get(1).getContents();
                
                if (list.get(0).getToken() == Token.BLOCK_PREFIX) {
                    
                    block = contents;
                    
                } else if (list.get(0).getToken() == Token.TRIPLE_DOT) {
                    
                    overflow = contents;
                }
                
            } else {
                
                Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Unrecognized expression.", location.getFileName(), location.getLineNumber());
            }
        }
        
        return new Parameters(mandatory, optional, block, overflow);
    }
    
    private static boolean isLevelUpToken(Token t) {
        
        return t == Token.LEFT_PARENTHESIS || t == Token.LEFT_SQUARE_BRACKET || t == Token.LEFT_CURLY_BRACKET;
    }
    
    private static boolean isLevelDownToken(Token t) {
        
        return t == Token.RIGHT_PARENTHESIS || t == Token.RIGHT_SQUARE_BRACKET || t == Token.RIGHT_CURLY_BRACKET;
    }
    
    private static boolean containsOnFirstLevel(TokenInfo[] tokens, Token... delimiters) {
        
        List<Token> tokenList = Arrays.asList(delimiters);
        
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
                
            } else if (level == 0 && tokenList.contains(info.getToken())) {
                
                return true;
            }
        }
        
        return false;
    }
    
    private static Token findOnFirstLevel(TokenInfo[] tokens, Token... delimiters) {
        
        List<Token> tokenList = Arrays.asList(delimiters);
        
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
                
            } else if (level == 0 && tokenList.contains(info.getToken())) {
                
                return info.getToken();
            }
        }
        
        return null;
    }
    
    private static boolean containsOnFirstLevelSequentially(TokenInfo[] tokens, Token first, Token second) {
        
        boolean foundFirst = false;
        
        int level = 0;
        for (TokenInfo info : tokens) {
            
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
                
            } else if (level == 0 && !foundFirst && info.getToken() == first) {
                
                foundFirst = true;
                
            } else if (level == 0 && foundFirst && info.getToken() == second) {
                
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean containsOperatorOnFirstLevel(TokenInfo[] tokens) {
        
        List<Token> tokenList = BinaryOperator.getOperators();
        
        int level = 0;
        for (int i = 0; i < tokens.length; i++) {
            
            TokenInfo info = tokens[i];
            
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
                
            } else if (level == 0 && tokenList.contains(info.getToken()) && checkOperator(tokens, i, info.getToken())) {
                
                return true;
            }
        }
        
        return false;
    }
    
    private static Token findOperatorOnFirstLevel(TokenInfo[] tokens) {
        
        List<Token> tokenList = BinaryOperator.getOperators();
        
        int level = 0, precedenceIndex = tokenList.size();
        for (int i = 0; i < tokens.length; i++) {
            
            TokenInfo info = tokens[i];
            
            if (isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (isLevelDownToken(info.getToken())) {
                
                level--;
                
            } else if (level == 0 && tokenList.contains(info.getToken()) && checkOperator(tokens, i, info.getToken())) {
                
                // Order of operations
                int index = tokenList.indexOf(info.getToken());
                
                if (index < precedenceIndex) {
                    
                    precedenceIndex = index;
                }
            }
        }
        
        if (precedenceIndex < tokenList.size()) {
            
            return tokenList.get(precedenceIndex);
        }
        
        return null;
    }
}
