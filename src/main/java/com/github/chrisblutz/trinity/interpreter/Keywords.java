package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.helpers.KeywordHelper;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Keywords {
    
    private static List<Token> keywords = new ArrayList<>();
    private static Map<Token, KeywordHelper> helpers = new HashMap<>();
    
    public static void register(Token keyword, KeywordHelper helper) {
        
        keywords.add(keyword);
        helpers.put(keyword, helper);
    }
    
    public static List<Token> getTokens() {
        
        return keywords;
    }
    
    public static KeywordHelper getHelper(Token token) {
        
        return helpers.get(token);
    }
}
