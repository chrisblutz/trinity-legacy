package com.github.chrisblutz.trinity.parser.comments;

import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class CommentUtils {
    
    public static String stripCommentSymbol(String comment) {
        
        if (comment.startsWith(Token.SINGLE_LINE_COMMENT.getLiteral())) {
            
            return comment.substring(1).trim();
            
        } else {
            
            return comment.trim();
        }
    }
}
