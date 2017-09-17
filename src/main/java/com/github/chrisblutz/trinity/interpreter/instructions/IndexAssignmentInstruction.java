package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.AssignmentOperators;
import com.github.chrisblutz.trinity.interpreter.BinaryOperator;
import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class IndexAssignmentInstruction extends Instruction {
    
    private Token operator;
    private InstructionSet object, value;
    private InstructionSet[] indices;
    
    public IndexAssignmentInstruction(Token operator, InstructionSet object, InstructionSet[] indices, InstructionSet value, Location location) {
        
        super(location);
        
        this.operator = operator;
        this.object = object;
        this.indices = indices;
        this.value = value;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public InstructionSet getObject() {
        
        return object;
    }
    
    public InstructionSet[] getIndices() {
        
        return indices;
    }
    
    public InstructionSet getValue() {
        
        return value;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject assignmentObject = getObject().evaluate(TYObject.NONE, runtime);
        TYObject opObj = getValue().evaluate(TYObject.NONE, runtime);
        
        TYObject[] params = new TYObject[getIndices().length];
        for (int i = 0; i < getIndices().length; i++) {
            
            params[i] = getIndices()[i].evaluate(TYObject.NONE, runtime);
        }
        
        TYObject[] paramsExtended = new TYObject[params.length + 1];
        System.arraycopy(params, 0, paramsExtended, 0, params.length);
        
        if (getOperator() == Token.ASSIGNMENT_OPERATOR) {
            
            paramsExtended[paramsExtended.length - 1] = opObj;
            assignmentObject.tyInvoke("[]=", runtime, null, null, paramsExtended);
            
        } else {
            
            TYObject currentObj = assignmentObject.tyInvoke("[]", runtime, null, null, params);
            
            if (getOperator() == Token.NIL_ASSIGNMENT_OPERATOR && currentObj == TYObject.NIL) {
                
                paramsExtended[paramsExtended.length - 1] = opObj;
                assignmentObject.tyInvoke("[]=", runtime, null, null, paramsExtended);
                
            } else {
                
                BinaryOperator op = AssignmentOperators.getOperator(getOperator());
                opObj = op.operate(currentObj, opObj, runtime);
                
                paramsExtended[paramsExtended.length - 1] = opObj;
                assignmentObject.tyInvoke("[]=", runtime, null, null, paramsExtended);
            }
        }
        
        return opObj;
    }
}
