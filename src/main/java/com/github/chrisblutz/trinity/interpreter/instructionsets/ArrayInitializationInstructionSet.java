package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class ArrayInitializationInstructionSet extends ObjectEvaluator {
    
    private ChainedInstructionSet[] arrayComponents;
    
    public ArrayInitializationInstructionSet(ChainedInstructionSet[] arrayComponents, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.arrayComponents = arrayComponents;
    }
    
    public ChainedInstructionSet[] getArrayComponents() {
        
        return arrayComponents;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        List<TYObject> objects = new ArrayList<>();
        
        for (ChainedInstructionSet set : getArrayComponents()) {
            
            objects.add(set.evaluate(TYObject.NONE, runtime, stackTrace));
        }
        
        return new TYArray(objects);
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "ArrayInitializationInstructionSet []");
        
        for (ObjectEvaluator child : arrayComponents) {
            
            str.append("\n").append(indent).append(child.toString(indent + "\t"));
        }
        
        return str.toString();
    }
}
