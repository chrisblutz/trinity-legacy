package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.AssignmentOperators;
import com.github.chrisblutz.trinity.interpreter.BinaryOperator;
import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class AssignmentInstruction extends Instruction {
    
    private Token operator;
    private InstructionSet remainder, value;
    private VariableLocRetriever retriever;
    
    public AssignmentInstruction(Token operator, InstructionSet remainder, VariableLocRetriever retriever, InstructionSet value, Location location) {
        
        super(location);
        
        this.operator = operator;
        this.remainder = remainder;
        this.retriever = retriever;
        this.value = value;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public InstructionSet getRemainder() {
        
        return remainder;
    }
    
    public VariableLocRetriever getRetriever() {
        
        return retriever;
    }
    
    public InstructionSet getValue() {
        
        return value;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject newThis = getRemainder().evaluate(TYObject.NONE, runtime);
        VariableLoc loc = getRetriever().evaluate(newThis, runtime);
        
        if (loc.checkScope(runtime)) {
            
            TYObject opObj = getValue().evaluate(TYObject.NONE, runtime);
            
            if (getOperator() == Token.ASSIGNMENT_OPERATOR) {
                
                VariableManager.put(loc, opObj);
                
            } else {
                
                TYObject currentObj = VariableManager.getVariable(loc);
                
                if (getOperator() == Token.NIL_ASSIGNMENT_OPERATOR && currentObj == TYObject.NIL) {
                    
                    VariableManager.put(loc, opObj);
                    
                } else {
                    
                    BinaryOperator op = AssignmentOperators.getOperator(getOperator());
                    opObj = op.operate(currentObj, opObj, runtime);
                    
                    VariableManager.put(loc, opObj);
                }
            }
            
            return opObj;
            
        } else {
            
            loc.getScope().reportAssignmentViolation(runtime);
            return TYObject.NIL;
        }
    }
}
