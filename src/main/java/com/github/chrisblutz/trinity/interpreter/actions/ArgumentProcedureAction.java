package com.github.chrisblutz.trinity.interpreter.actions;

import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.threading.TYThread;


/**
 * @author Christopher Lutz
 */
public class ArgumentProcedureAction implements ProcedureAction {
    
    private InstructionSet set;
    
    public ArgumentProcedureAction(InstructionSet set) {
        
        this.set = set;
    }
    
    @Override
    public TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params) {
        
        return set.evaluate(thisObj, runtime);
    }
}
