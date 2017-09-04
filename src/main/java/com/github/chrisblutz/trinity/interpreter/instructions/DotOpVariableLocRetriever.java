package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;


/**
 * @author Christopher Lutz
 */
public class DotOpVariableLocRetriever implements VariableLocRetriever {
    
    private Instruction firstSet;
    private VariableLocRetriever retriever;
    
    public DotOpVariableLocRetriever(Instruction firstSet, VariableLocRetriever retriever) {
        
        this.firstSet = firstSet;
        this.retriever = retriever;
    }
    
    public Instruction getFirstSet() {
        
        return firstSet;
    }
    
    public VariableLocRetriever getRetriever() {
        
        return retriever;
    }
    
    @Override
    public VariableLoc evaluate(TYObject thisObj, TYRuntime runtime) {
        
        TYObject newThis = getFirstSet().evaluate(thisObj, runtime);
        
        return getRetriever().evaluate(newThis, runtime);
    }
}
