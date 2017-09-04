package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.helpers.KeywordExpressionHelper;
import com.github.chrisblutz.trinity.interpreter.helpers.PostConstraintHelper;
import com.github.chrisblutz.trinity.interpreter.helpers.SingleComponentKeywordExpressionHelper;
import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.*;


/**
 * @author Christopher Lutz
 */
public class KeywordExpressions {
    
    private static List<Token> keywords = new ArrayList<>();
    private static Map<Token, Integer> keywordComponents = new HashMap<>();
    private static Map<Token, Boolean> keywordRigid = new HashMap<>();
    private static Map<Token, Boolean> keywordAddDirect = new HashMap<>();
    private static Map<Token, Token> keywordDelimiters = new HashMap<>();
    private static Map<Token, KeywordExpressionHelper> keywordHelpers = new HashMap<>();
    
    private static Map<Block, Token> previousTokens = new WeakHashMap<>();
    private static Map<Block, InstructionSet> previousSets = new WeakHashMap<>();
    
    private static Map<Token, Token[]> constraints = new HashMap<>();
    private static Map<Token, String> constraintMessages = new HashMap<>();
    private static Map<Token, PostConstraintHelper> constraintHelpers = new HashMap<>();
    
    public static void registerKeywordExpression(Token keyword, boolean rigid, boolean addDirect, SingleComponentKeywordExpressionHelper helper) {
        
        registerKeywordExpression(keyword, 1, rigid, addDirect, null, helper);
    }
    
    public static void registerKeywordExpression(Token keyword, int components, boolean rigid, boolean addDirect, Token delimiter, KeywordExpressionHelper helper) {
        
        keywords.add(keyword);
        keywordComponents.put(keyword, components);
        keywordRigid.put(keyword, rigid);
        keywordAddDirect.put(keyword, addDirect);
        keywordDelimiters.put(keyword, delimiter);
        keywordHelpers.put(keyword, helper);
    }
    
    public static void registerKeywordConstraint(Token keyword, Token[] previousAllowed, String errorMessage, PostConstraintHelper helper) {
        
        constraints.put(keyword, previousAllowed);
        constraintMessages.put(keyword, errorMessage);
        constraintHelpers.put(keyword, helper);
    }
    
    public static boolean isKeyword(Token token) {
        
        return keywords.contains(token);
    }
    
    public static int getKeywordComponents(Token token) {
        
        return keywordComponents.getOrDefault(token, 1);
    }
    
    public static boolean isKeywordRigid(Token token) {
        
        return keywordRigid.getOrDefault(token, true);
    }
    
    public static boolean getKeywordAddDirect(Token token){
        
        return keywordAddDirect.get(token);
    }
    
    public static Token getKeywordDelimiter(Token token) {
        
        return keywordDelimiters.get(token);
    }
    
    public static KeywordExpressionHelper getKeywordHelper(Token token) {
        
        return keywordHelpers.get(token);
    }
    
    public static Token[] getConstraints(Token token) {
        
        if (constraints.containsKey(token)) {
            
            return constraints.get(token);
            
        } else {
            
            return new Token[0];
        }
    }
    
    public static String getConstraintMessage(Token token) {
        
        return constraintMessages.getOrDefault(token, "");
    }
    
    public static boolean runConstraintHelper(Block block, Token token, InstructionSet set) {
        
        PostConstraintHelper helper = constraintHelpers.get(token);
        
        if (helper != null) {
            
            return helper.postConstraint(set, previousSets.get(block));
        }
        
        return true;
    }
    
    public static boolean checkConstraints(Block block, Token token) {
        
        return getConstraints(token).length == 0 || Arrays.asList(getConstraints(token)).contains(previousTokens.get(block));
    }
    
    public static void updatePrevious(Block block, Token token, InstructionSet set) {
        
        previousTokens.put(block, token);
        previousSets.put(block, set);
    }
}
