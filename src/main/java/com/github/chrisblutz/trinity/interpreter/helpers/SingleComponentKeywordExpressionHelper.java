package com.github.chrisblutz.trinity.interpreter.helpers;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;


/**
 * @author Christopher Lutz
 */
public abstract class SingleComponentKeywordExpressionHelper implements KeywordExpressionHelper {
    
    public abstract InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location);
    
    @Override
    public InstructionSet interpret(InstructionSet[] sets, ProcedureAction next, Location location) {
        
        InstructionSet set = null;
        if (sets.length > 0) {
            
            set = sets[0];
        }
        
        return interpret(set, next, location);
    }
}
