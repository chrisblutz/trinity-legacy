package com.github.chrisblutz.trinity.interpreter.actions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.threading.TYThread;


/**
 * @author Christopher Lutz
 */
public class VariableProcedureAction implements ProcedureAction {
    
    private String initStackElementName;
    private Location location;
    private TYClass container;
    private InstructionSet set;
    
    public VariableProcedureAction(String initStackElementName, Location location, TYClass container, InstructionSet set) {
        
        this.initStackElementName = initStackElementName;
        this.location = location;
        this.container = container;
        this.set = set;
    }
    
    @Override
    public TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params) {
        
        TYThread current = TYThread.getCurrentThread();
        
        current.getTrinityStack().add(container.getName(), initStackElementName, location.getFileName(), location.getLineNumber());
        
        TYObject result = set.evaluate(TYObject.NONE, runtime);
        
        current.getTrinityStack().pop();
        
        return result;
    }
}
