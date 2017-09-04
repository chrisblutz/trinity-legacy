package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.runner.Runner;


/**
 * @author Christopher Lutz
 */
public abstract class Instruction {
    
    private Location location;
    
    public Instruction(Location location) {
        
        this.location = location;
    }
    
    public Location getLocation() {
        
        return location;
    }
    
    public void updateLocation() {
        
        Runner.updateLocation(location.getFileName(), location.getLineNumber());
    }
    
    protected abstract TYObject evaluate(TYObject thisObj, TYRuntime runtime);
}
