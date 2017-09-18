package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class Operators {
    
    private static List<Token> operatorMethods = new ArrayList<>();
    
    public static void registerOperatorMethod(LogicalOperator operator) {
        
        operatorMethods.add(operator.getToken());
    }
    
    public static void registerOperatorMethod(BinaryOperator operator) {
        
        operatorMethods.add(operator.getToken());
    }
    
    public static void registerOperatorMethod(UnaryOperator operator) {
        
        operatorMethods.add(operator.getToken());
    }
    
    public static boolean isOperatorMethod(Token token) {
        
        return operatorMethods.contains(token);
    }
}
