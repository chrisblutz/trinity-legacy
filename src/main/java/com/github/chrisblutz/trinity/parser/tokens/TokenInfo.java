package com.github.chrisblutz.trinity.parser.tokens;

/**
 * @author Christopher Lutz
 */
public class TokenInfo {
    
    private Token token;
    private String contents;
    
    public TokenInfo(Token token, String contents) {
        
        this.token = token;
        this.contents = contents;
    }
    
    public Token getToken() {
        
        return token;
    }
    
    public String getContents() {
        
        return contents;
    }
    
    @Override
    public String toString() {
        
        return token + " -> " + getContents();
    }
}
