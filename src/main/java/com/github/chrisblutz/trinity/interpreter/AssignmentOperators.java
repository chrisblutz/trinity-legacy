package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class AssignmentOperators {
    
    private static Map<Token, BinaryOperator> assignmentOperators = new HashMap<>();
    private static Token[] tokens = new Token[0];
    
    public static void registerAssignmentOperator(Token token, BinaryOperator operator) {
        
        assignmentOperators.put(token, operator);
        tokens = assignmentOperators.keySet().toArray(new Token[assignmentOperators.size()]);
    }
    
    public static BinaryOperator getOperator(Token token) {
        
        return assignmentOperators.get(token);
    }
    
    public static Token[] getAssignmentTokens() {
        
        return tokens;
    }
}
