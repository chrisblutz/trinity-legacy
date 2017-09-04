package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class IndexAccessInstruction extends Instruction {
    
    private InstructionSet[] indices;
    private TYProcedure procedure;
    
    public IndexAccessInstruction(InstructionSet[] indices, TYProcedure procedure, Location location) {
        
        super(location);
        
        this.indices = indices;
        this.procedure = procedure;
    }
    
    public InstructionSet[] getIndices() {
        
        return indices;
    }
    
    public TYProcedure getProcedure() {
        
        return procedure;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        List<TYObject> indices = new ArrayList<>();
        for (InstructionSet index : getIndices()) {
            
            indices.add(index.evaluate(TYObject.NONE, runtime));
        }
        
        TYObject[] indexArray = indices.toArray(new TYObject[indices.size()]);
        
        return thisObj.tyInvoke("[]", runtime, getProcedure(), runtime, indexArray);
    }
}
