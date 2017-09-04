package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.helpers.Declaration;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Declarations {
    
    private static List<Token> tokens = new ArrayList<>();
    private static Map<Token, Integer> minimums = new HashMap<>();
    private static Map<Token, Integer> maximums = new HashMap<>();
    private static Map<Token, Declaration> declarations = new HashMap<>();
    
    public static void register(Token token, int minTokens, int maxTokens, Declaration declaration) {
        
        tokens.add(token);
        minimums.put(token, minTokens);
        maximums.put(token, maxTokens);
        declarations.put(token, declaration);
    }
    
    public static List<Token> getTokens() {
        
        return tokens;
    }
    
    public static boolean checkSize(Token token, Line line) {
        
        int min = minimums.get(token);
        int max = maximums.get(token);
        
        return (min == 0 || line.size() >= min) && (max == 0 || line.size() <= max);
    }
    
    public static Declaration getDeclaration(Token token) {
        
        return declarations.get(token);
    }
}
