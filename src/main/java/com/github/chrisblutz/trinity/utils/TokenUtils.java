package com.github.chrisblutz.trinity.utils;

import com.github.chrisblutz.trinity.interpreter.ExpressionInterpreter;
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
            
            if (ExpressionInterpreter.isLevelUpToken(info.getToken())) {
                
                level++;
                
            } else if (ExpressionInterpreter.isLevelDownToken(info.getToken())) {
                
                level--;
                
            } else if (level == 0 && tokenList.contains(info.getToken())) {
                
                return true;
            }
        }
        
        return false;
    }
}
