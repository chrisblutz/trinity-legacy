package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;


/**
 * @author Christopher Lutz
 */
public class ReturnInstructionSet extends InstructionSet {
    
    private InstructionSet expression;
    
    public ReturnInstructionSet(InstructionSet expression, Location location) {
        
        super(new Instruction[0], location);
        
        this.expression = expression;
    }
    
    public InstructionSet getExpression() {
        
        return expression;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject result = TYObject.NIL;
        
        if (getExpression() != null) {
            
            result = getExpression().evaluate(TYObject.NONE, runtime);
        }
        
        runtime.setReturning(true);
        runtime.setReturnObject(result);
        
        return result;
    }
}
