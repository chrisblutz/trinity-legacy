package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Keywords;
import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;


/**
 * @author Christopher Lutz
 */
public class KeywordInstruction extends Instruction {
    
    private TokenInfo keyword;
    
    public KeywordInstruction(TokenInfo keyword, Location location) {
        
        super(location);
        
        this.keyword = keyword;
    }
    
    public TokenInfo getKeyword() {
        
        return keyword;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        return Keywords.getHelper(getKeyword().getToken()).evaluate(thisObj, getKeyword(), getLocation(), runtime);
    }
}
