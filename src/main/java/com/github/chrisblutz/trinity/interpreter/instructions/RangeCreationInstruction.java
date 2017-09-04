package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class RangeCreationInstruction extends Instruction {
    
    private Token divider;
    private InstructionSet beginningValue, endingValue;
    
    public RangeCreationInstruction(Token divider, InstructionSet beginningValue, InstructionSet endingValue, Location location) {
        
        super(location);
        
        this.divider = divider;
        this.beginningValue = beginningValue;
        this.endingValue = endingValue;
    }
    
    public Token getDivider() {
        
        return divider;
    }
    
    public InstructionSet getBeginningValue() {
        
        return beginningValue;
    }
    
    public InstructionSet getEndingValue() {
        
        return endingValue;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject begin = getBeginningValue().evaluate(TYObject.NONE, runtime);
        TYObject end = getEndingValue().evaluate(TYObject.NONE, runtime);
        boolean exclude = getDivider() == Token.TRIPLE_DOT;
        
        return TrinityNatives.newInstance("Trinity.Range", runtime, begin, end, TrinityNatives.getObjectFor(exclude));
    }
}
