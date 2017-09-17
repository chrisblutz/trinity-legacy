package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;


/**
 * @author Christopher Lutz
 */
public class GlobalVariableInstruction extends Instruction {
    
    private String name;
    
    public GlobalVariableInstruction(String name, Location location) {
        
        super(location);
        
        this.name = name;
    }
    
    public String getName() {
        
        return name;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        if (Variables.hasGlobalVariable(getName())) {
            
            return VariableManager.getVariable(Variables.getGlobalVariable(getName()));
            
        } else {
            
            Errors.throwError("Trinity.Errors.FieldNotFoundError", runtime, "Global field '" + getName() + "' not found.");
        }
        
        return TYObject.NIL;
    }
}
