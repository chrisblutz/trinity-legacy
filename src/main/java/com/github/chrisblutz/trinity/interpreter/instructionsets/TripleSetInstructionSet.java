package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class TripleSetInstructionSet extends ObjectEvaluator {
    
    public enum TokenSet {
        
        TERNARY_OP
    }
    
    private TokenSet set;
    private ChainedInstructionSet leftSet, middleSet, rightSet;
    private Token leftDivider, rightDivider;
    
    public TripleSetInstructionSet(TokenSet set, ChainedInstructionSet leftSet, ChainedInstructionSet middleSet, ChainedInstructionSet rightSet, Token leftDivider, Token rightDivider, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.set = set;
        this.leftSet = leftSet;
        this.middleSet = middleSet;
        this.rightSet = rightSet;
        this.leftDivider = leftDivider;
        this.rightDivider = rightDivider;
    }
    
    public TokenSet getSet() {
        
        return set;
    }
    
    public ChainedInstructionSet getLeftSet() {
        
        return leftSet;
    }
    
    public ChainedInstructionSet getMiddleSet() {
        
        return middleSet;
    }
    
    public ChainedInstructionSet getRightSet() {
        
        return rightSet;
    }
    
    public Token getLeftDivider() {
        
        return leftDivider;
    }
    
    public Token getRightDivider() {
        
        return rightDivider;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        switch (set) {
            
            case TERNARY_OP:
                
                TYObject leftObj = getLeftSet().evaluate(TYObject.NONE, runtime);
                if (TrinityNatives.toBoolean(leftObj)) {
                    
                    return getMiddleSet().evaluate(TYObject.NONE, runtime);
                    
                } else {
                    
                    return getRightSet().evaluate(TYObject.NONE, runtime);
                }
        }
        
        return TYObject.NONE;
    }
    
    @Override
    public String toString(String indent) {
        
        return "TripleSetInstructionSet [" + getSet() + "]";
    }
}
