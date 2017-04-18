package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class ChainedInstructionSet extends ObjectEvaluator {
    
    private List<ObjectEvaluator> children = new ArrayList<>();
    
    public ChainedInstructionSet(ObjectEvaluator[] evaluators, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        children.addAll(Arrays.asList(evaluators));
    }
    
    public List<ObjectEvaluator> getChildren() {
        
        return children;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        for (ObjectEvaluator child : getChildren()) {
            
            thisObj = child.evaluate(thisObj, runtime, stackTrace);
        }
        
        return thisObj;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "ChainedInstructionSet [Set Count: " + getChildren().size() + "]");
        
        for (ObjectEvaluator child : getChildren()) {
            
            str.append("\n").append(indent).append(child.toString(indent + "\t"));
        }
        
        return str.toString();
    }
}
