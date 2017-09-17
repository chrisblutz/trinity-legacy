package com.github.chrisblutz.trinity.interpreter;


import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public abstract class BinaryOperator {
    
    private static List<Token> tokens = new ArrayList<>();
    private static Map<Token, BinaryOperator> operators = new HashMap<>();
    
    private Token token;
    
    public BinaryOperator(Token token) {
        
        this.token = token;
        
        tokens.add(token);
        operators.put(token, this);
    }
    
    public Token getToken() {
        
        return token;
    }
    
    public abstract TYObject operate(TYObject first, TYObject second, TYRuntime runtime);
    
    public static List<Token> getOperators() {
        
        return tokens;
    }
    
    public static BinaryOperator getOperator(Token token) {
        
        return operators.get(token);
    }
}
