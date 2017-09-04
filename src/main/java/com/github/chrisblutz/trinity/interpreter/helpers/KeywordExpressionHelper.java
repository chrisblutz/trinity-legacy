package com.github.chrisblutz.trinity.interpreter.helpers;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;


/**
 * @author Christopher Lutz
 */
public interface KeywordExpressionHelper {
    
    InstructionSet interpret(InstructionSet[] sets, ProcedureAction next, Location location);
}
