package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class DoubleSetInstructionSet extends ObjectEvaluator {
    
    public enum TokenSet {
        
        RANGE
    }
    
    private TokenSet set;
    private ChainedInstructionSet leftSet, rightSet;
    private Token divider;
    
    public DoubleSetInstructionSet(TokenSet set, ChainedInstructionSet leftSet, ChainedInstructionSet rightSet, Token divider, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.set = set;
        this.leftSet = leftSet;
        this.rightSet = rightSet;
        this.divider = divider;
    }
    
    public TokenSet getSet() {
        
        return set;
    }
    
    public ChainedInstructionSet getLeftSet() {
        
        return leftSet;
    }
    
    public ChainedInstructionSet getRightSet() {
        
        return rightSet;
    }
    
    public Token getDivider() {
        
        return divider;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        switch (set) {
            
            case RANGE:
                
                TYObject leftObj = getLeftSet().evaluate(TYObject.NONE, runtime);
                TYObject rightObj = getRightSet().evaluate(TYObject.NONE, runtime);
                boolean exclude = getDivider() == Token.TRIPLE_DOT;
                
                return TrinityNatives.newInstance("Trinity.Range", runtime, leftObj, rightObj, TrinityNatives.getObjectFor(exclude));
        }
        
        return TYObject.NONE;
    }
    
    @Override
    public String toString(String indent) {
        
        return "MultiTokenInstructionSet [" + getSet() + "]";
    }
}
