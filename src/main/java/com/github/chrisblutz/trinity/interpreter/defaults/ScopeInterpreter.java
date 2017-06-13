package com.github.chrisblutz.trinity.interpreter.defaults;

import com.github.chrisblutz.trinity.interpreter.DeclarationInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.Scope;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class ScopeInterpreter extends DeclarationInterpreter {
    
    @Override
    public Token getTokenIdentifier() {
        
        return Token.SCOPE_MODIFIER;
    }
    
    @Override
    public void interpret(Line line, Block nextBlock, InterpretEnvironment env, String fileName, File fullFile) {
        
        if (line.get(0).getToken() == Token.SCOPE_MODIFIER) {
            
            TokenInfo info = line.get(0);
            Scope scope = Scope.PUBLIC;
            
            if (info.getContents().contentEquals(Token.PRIVATE_SCOPE.getReadable())) {
                
                scope = Scope.PRIVATE;
                
            } else if (info.getContents().contentEquals(Token.PROTECTED_SCOPE.getReadable())) {
                
                scope = Scope.PROTECTED;
                
            } else if (info.getContents().contentEquals(Token.MODULE_PROTECTED_SCOPE.getReadable())) {
                
                scope = Scope.MODULE_PROTECTED;
                
            } else if (info.getContents().contentEquals(Token.PUBLIC_SCOPE.getReadable())) {
                
                scope = Scope.PUBLIC;
            }
            
            if (nextBlock != null) {
                
                InterpretEnvironment newEnv = env.append(scope);
                interpretChildren(nextBlock, newEnv);
                env.setScope(scope);
                
            } else {
                
                env.setScope(scope);
            }
        }
    }
}
