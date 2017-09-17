package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public enum Scope {
    
    PRIVATE(Token.PRIVATE_SCOPE.getReadable()), PROTECTED(Token.PROTECTED_SCOPE.getReadable()),
    MODULE_PROTECTED(Token.MODULE_PROTECTED_SCOPE.getReadable()), PUBLIC(Token.PUBLIC_SCOPE.getReadable());
    
    private String str;
    
    Scope(String str) {
        
        this.str = str;
    }
    
    @Override
    public String toString() {
        
        return str;
    }
    
    public void reportAccessViolation(TYRuntime runtime) {
        
        Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Cannot access value of field marked '" + this.toString() + "' here.");
    }
    
    public void reportAssignmentViolation(TYRuntime runtime) {
        
        Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Cannot set value of field marked '" + this.toString() + "' here.");
    }
}
