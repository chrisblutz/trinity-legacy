package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class AssignmentInstructionSet extends ObjectEvaluator {
    
    private ChainedInstructionSet assignmentObject;
    private VariableLocInstructionSet varLocInstructionSet;
    private Token operator;
    private ChainedInstructionSet value;
    
    public AssignmentInstructionSet(ChainedInstructionSet assignmentObject, Token operator, ChainedInstructionSet value, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        
        this.assignmentObject = assignmentObject;
        ObjectEvaluator evaluator = assignmentObject.getChildren().remove(assignmentObject.getChildren().size() - 1);
        
        if (evaluator.getClass() == InstructionSet.class) {
            
            TokenInfo[] tokens = ((InstructionSet) evaluator).getTokens();
            varLocInstructionSet = new VariableLocInstructionSet(tokens);
            
        } else {
            
            Errors.throwSyntaxError("Trinity.Errors.ParseError", "Invalid left-hand expression.", fileName, lineNumber);
        }
        this.operator = operator;
        this.value = value;
    }
    
    public ChainedInstructionSet getAssignmentObject() {
        
        return assignmentObject;
    }
    
    public Token getOperator() {
        
        return operator;
    }
    
    public ChainedInstructionSet getValue() {
        
        return value;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        TYObject opObj = getValue().evaluate(TYObject.NONE, runtime);
        
        if (varLocInstructionSet != null) {
            
            TYObject cISThis = getAssignmentObject().evaluate(TYObject.NONE, runtime);
            VariableLoc loc = varLocInstructionSet.evaluate(cISThis, runtime);
            
            if (loc.isConstant()) {
                
                Errors.throwError("Trinity.Errors.AssignmentError", runtime, "Unable to reassign a value to a constant.");
            }
            
            if (checkScope(loc, runtime)) {
                
                if (getOperator() == Token.ASSIGNMENT_OPERATOR) {
                    
                    VariableManager.put(loc, opObj);
                    
                } else {
                    
                    TYObject current = VariableManager.getVariable(loc);
                    
                    if (getOperator() == Token.NIL_ASSIGNMENT_OPERATOR && current == TYObject.NIL) {
                        
                        VariableManager.put(loc, opObj);
                        
                    } else {
                        
                        if (getOperator() == Token.PLUS_EQUAL) {
                            
                            opObj = current.tyInvoke("+", runtime, null, null, opObj);
                            
                        } else if (getOperator() == Token.MINUS_EQUAL) {
                            
                            opObj = current.tyInvoke("-", runtime, null, null, opObj);
                            
                        } else if (getOperator() == Token.MULTIPLY_EQUAL) {
                            
                            opObj = current.tyInvoke("*", runtime, null, null, opObj);
                            
                        } else if (getOperator() == Token.DIVIDE_EQUAL) {
                            
                            opObj = current.tyInvoke("/", runtime, null, null, opObj);
                            
                        } else if (getOperator() == Token.MODULUS_EQUAL) {
                            
                            opObj = current.tyInvoke("%", runtime, null, null, opObj);
                            
                        } else if (getOperator() == Token.BITWISE_AND_EQUAL) {
                            
                            if (current.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) && opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                                
                                long thisLong = TrinityNatives.toLong(current);
                                long otherLong = TrinityNatives.toLong(opObj);
                                opObj = new TYLong(thisLong & otherLong);
                                
                            } else {
                                
                                int thisInt = TrinityNatives.toInt(current);
                                int otherInt = TrinityNatives.toInt(opObj);
                                opObj = new TYInt(thisInt & otherInt);
                            }
                            
                        } else if (getOperator() == Token.BITWISE_OR_EQUAL) {
                            
                            if (current.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) && opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                                
                                long thisLong = TrinityNatives.toLong(current);
                                long otherLong = TrinityNatives.toLong(opObj);
                                opObj = new TYLong(thisLong | otherLong);
                                
                            } else {
                                
                                int thisInt = TrinityNatives.toInt(current);
                                int otherInt = TrinityNatives.toInt(opObj);
                                opObj = new TYInt(thisInt | otherInt);
                            }
                            
                        } else if (getOperator() == Token.BITWISE_XOR_EQUAL) {
                            
                            if (current.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) && opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                                
                                long thisLong = TrinityNatives.toLong(current);
                                long otherLong = TrinityNatives.toLong(opObj);
                                opObj = new TYLong(thisLong ^ otherLong);
                                
                            } else {
                                
                                int thisInt = TrinityNatives.toInt(current);
                                int otherInt = TrinityNatives.toInt(opObj);
                                opObj = new TYInt(thisInt ^ otherInt);
                            }
                            
                        } else if (getOperator() == Token.BIT_SHIFT_LEFT_EQUAL) {
                            
                            if (current.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) && opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                                
                                long thisLong = TrinityNatives.toLong(current);
                                long otherLong = TrinityNatives.toLong(opObj);
                                opObj = new TYLong(thisLong << otherLong);
                                
                            } else {
                                
                                int thisInt = TrinityNatives.toInt(current);
                                int otherInt = TrinityNatives.toInt(opObj);
                                opObj = new TYInt(thisInt << otherInt);
                            }
                            
                        } else if (getOperator() == Token.BIT_SHIFT_RIGHT_EQUAL) {
                            
                            if (current.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) && opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                                
                                long thisLong = TrinityNatives.toLong(current);
                                long otherLong = TrinityNatives.toLong(opObj);
                                opObj = new TYLong(thisLong >> otherLong);
                                
                            } else {
                                
                                int thisInt = TrinityNatives.toInt(current);
                                int otherInt = TrinityNatives.toInt(opObj);
                                opObj = new TYInt(thisInt >> otherInt);
                            }
                            
                        } else if (getOperator() == Token.BIT_SHIFT_LOGICAL_RIGHT_EQUAL) {
                            
                            if (current.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long")) && opObj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Long"))) {
                                
                                long thisLong = TrinityNatives.toLong(current);
                                long otherLong = TrinityNatives.toLong(opObj);
                                opObj = new TYLong(thisLong >>> otherLong);
                                
                            } else {
                                
                                int thisInt = TrinityNatives.toInt(current);
                                int otherInt = TrinityNatives.toInt(opObj);
                                opObj = new TYInt(thisInt >>> otherInt);
                            }
                        }
                        
                        VariableManager.put(loc, opObj);
                    }
                }
                
            } else {
                
                Errors.throwError("Trinity.Errors.ScopeError", "Cannot set value of field marked '" + loc.getScope().toString() + "' here.");
            }
        }
        
        return opObj;
    }
    
    private boolean checkScope(VariableLoc loc, TYRuntime runtime) {
        
        if (loc.getContainerClass() == null) {
            
            return true;
        }
        
        switch (loc.getScope()) {
            
            case PUBLIC:
                
                return true;
            
            case MODULE_PROTECTED:
                
                return loc.getContainerClass().getModule() == runtime.getModule();
            
            case PROTECTED:
                
                return runtime.getTyClass().isInstanceOf(loc.getContainerClass());
            
            case PRIVATE:
                
                return loc.getContainerClass() == runtime.getTyClass();
            
            default:
                
                return false;
        }
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        String str = indent + "AssignmentInstructionSet [" + getOperator() + "]";
        
        str += "\n" + indent + getAssignmentObject().toString(indent + "\t");
        
        str += "\n" + indent + getValue().toString(indent + "\t");
        
        return str;
    }
}
