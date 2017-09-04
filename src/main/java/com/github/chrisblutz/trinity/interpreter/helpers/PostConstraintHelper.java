package com.github.chrisblutz.trinity.interpreter.helpers;

import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;


/**
 * @author Christopher Lutz
 */
public abstract class PostConstraintHelper {
    
    public abstract boolean postConstraint(InstructionSet set, InstructionSet previous);
}
