package com.github.chrisblutz.trinity.utils;

import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.util.Arrays;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TokenUtils {
    
    public static boolean containsOnFirstLevel(TokenInfo[] infoSets, Token... tokens) {
        
        List<Token> tokenList = Arrays.asList(tokens);
        
        int level = 0;
        for (TokenInfo info : infoSets) {
            
            if (info.getToken() == Token.LEFT_PARENTHESIS || info.getToken() == Token.LEFT_SQUARE_BRACKET) {
                
                level += 1;
                
            } else if (info.getToken() == Token.RIGHT_PARENTHESIS || info.getToken() == Token.RIGHT_SQUARE_BRACKET) {
                
                level -= 1;
                
            } else if (level == 0 && tokenList.contains(info.getToken())) {
                
                return true;
            }
        }
        
        return false;
    }
}
