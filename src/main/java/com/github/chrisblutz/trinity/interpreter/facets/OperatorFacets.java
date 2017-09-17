package com.github.chrisblutz.trinity.interpreter.facets;

import com.github.chrisblutz.trinity.interpreter.AssignmentOperators;
import com.github.chrisblutz.trinity.interpreter.BinaryOperator;
import com.github.chrisblutz.trinity.interpreter.LogicalOperator;
import com.github.chrisblutz.trinity.interpreter.UnaryOperator;
import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class OperatorFacets {
    
    public static void registerFacets() {
        
        // Logical Operators
        new LogicalOperator(Token.AND) {
            
            @Override
            public TYBoolean operate(TYObject first, InstructionSet second, TYRuntime runtime) {
                
                if (TrinityNatives.toBoolean(first)) {
                    
                    return TYBoolean.valueFor(TrinityNatives.toBoolean(second.evaluate(TYObject.NONE, runtime)));
                    
                } else {
                    
                    return TYBoolean.FALSE;
                }
            }
        };
        new LogicalOperator(Token.OR) {
            
            @Override
            public TYBoolean operate(TYObject first, InstructionSet second, TYRuntime runtime) {
                
                if (TrinityNatives.toBoolean(first)) {
                    
                    return TYBoolean.TRUE;
                    
                } else {
                    
                    return TYBoolean.valueFor(TrinityNatives.toBoolean(second.evaluate(TYObject.NONE, runtime)));
                }
            }
        };
        
        // Binary operators
        // Order of operations is from bottom to top
        // Operators at the top are separated out first,
        // meaning that the ones at the bottom are executed first
        //
        // Ex. 10 + 10 * 10
        //  -> (10) + (10 * 10)
        //  -> (10) + ((10) * (10))
        
        BinaryOperator bitwiseOr = new BinaryOperator(Token.VERTICAL_BAR) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                if (TrinityNatives.isInstance(first, "Trinity.Long") || TrinityNatives.isInstance(second, "Trinity.Long")) {
                    
                    long firstLong = TrinityNatives.toLong(first);
                    long secondLong = TrinityNatives.toLong(second);
                    return new TYLong(firstLong | secondLong);
                    
                } else {
                    
                    int firstInt = TrinityNatives.toInt(first);
                    int secondInt = TrinityNatives.toInt(second);
                    return new TYInt(firstInt | secondInt);
                }
            }
        };
        BinaryOperator bitwiseXor = new BinaryOperator(Token.BITWISE_XOR) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                if (TrinityNatives.isInstance(first, "Trinity.Long") || TrinityNatives.isInstance(second, "Trinity.Long")) {
                    
                    long firstLong = TrinityNatives.toLong(first);
                    long secondLong = TrinityNatives.toLong(second);
                    return new TYLong(firstLong ^ secondLong);
                    
                } else {
                    
                    int firstInt = TrinityNatives.toInt(first);
                    int secondInt = TrinityNatives.toInt(second);
                    return new TYInt(firstInt ^ secondInt);
                }
            }
        };
        BinaryOperator bitwiseAnd = new BinaryOperator(Token.BLOCK_PREFIX) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                if (TrinityNatives.isInstance(first, "Trinity.Long") || TrinityNatives.isInstance(second, "Trinity.Long")) {
                    
                    long firstLong = TrinityNatives.toLong(first);
                    long secondLong = TrinityNatives.toLong(second);
                    return new TYLong(firstLong & secondLong);
                    
                } else {
                    
                    int firstInt = TrinityNatives.toInt(first);
                    int secondInt = TrinityNatives.toInt(second);
                    return new TYInt(firstInt & secondInt);
                }
            }
        };
        
        new BinaryOperator(Token.EQUAL_TO) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke("==", runtime, null, null, second);
            }
        };
        new BinaryOperator(Token.NOT_EQUAL_TO) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return TYBoolean.valueFor(!TrinityNatives.toBoolean(first.tyInvoke("==", runtime, null, null, second)));
            }
        };
        new BinaryOperator(Token.LESS_THAN) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                int comparisonInt = TrinityNatives.toInt(first.tyInvoke("compareTo", runtime, null, null, second));
                
                return comparisonInt < 0 ? TYBoolean.TRUE : TYBoolean.FALSE;
            }
        };
        new BinaryOperator(Token.LESS_THAN_OR_EQUAL_TO) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                int comparisonInt = TrinityNatives.toInt(first.tyInvoke("compareTo", runtime, null, null, second));
                
                return comparisonInt <= 0 ? TYBoolean.TRUE : TYBoolean.FALSE;
            }
        };
        new BinaryOperator(Token.GREATER_THAN) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                int comparisonInt = TrinityNatives.toInt(first.tyInvoke("compareTo", runtime, null, null, second));
                
                return comparisonInt > 0 ? TYBoolean.TRUE : TYBoolean.FALSE;
            }
        };
        new BinaryOperator(Token.GREATER_THAN_OR_EQUAL_TO) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                int comparisonInt = TrinityNatives.toInt(first.tyInvoke("compareTo", runtime, null, null, second));
                
                return comparisonInt >= 0 ? TYBoolean.TRUE : TYBoolean.FALSE;
            }
        };
        
        BinaryOperator bitShiftLeft = new BinaryOperator(Token.CLASS_EXTENSION) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke("<<", runtime, null, null, second);
            }
        };
        BinaryOperator bitShiftRight = new BinaryOperator(Token.BIT_SHIFT_RIGHT) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke(">>", runtime, null, null, second);
            }
        };
        BinaryOperator bitShiftRightLogical = new BinaryOperator(Token.BIT_SHIFT_LOGICAL_RIGHT) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke(">>>", runtime, null, null, second);
            }
        };
        
        BinaryOperator minus = new BinaryOperator(Token.MINUS) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke("-", runtime, null, null, second);
            }
        };
        BinaryOperator plus = new BinaryOperator(Token.PLUS) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                if (TrinityNatives.isInstance(second, "Trinity.String")) {
                    
                    first = first.tyInvoke("toString", runtime, null, null);
                }
                
                return first.tyInvoke("+", runtime, null, null, second);
            }
        };
        BinaryOperator modulus = new BinaryOperator(Token.MODULUS) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke("%", runtime, null, null, second);
            }
        };
        BinaryOperator divide = new BinaryOperator(Token.DIVIDE) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                return first.tyInvoke("/", runtime, null, null, second);
            }
        };
        BinaryOperator multiply = new BinaryOperator(Token.MULTIPLY) {
            
            @Override
            public TYObject operate(TYObject first, TYObject second, TYRuntime runtime) {
                
                if (TrinityNatives.isInstance(second, "Trinity.String")) {
                    
                    return second.tyInvoke("*", runtime, null, null, first);
                    
                } else {
                    
                    return first.tyInvoke("*", runtime, null, null, second);
                }
            }
        };
        
        // Assignment Operators
        AssignmentOperators.registerAssignmentOperator(Token.ASSIGNMENT_OPERATOR, null);
        AssignmentOperators.registerAssignmentOperator(Token.NIL_ASSIGNMENT_OPERATOR, null);
        AssignmentOperators.registerAssignmentOperator(Token.PLUS_EQUAL, plus);
        AssignmentOperators.registerAssignmentOperator(Token.MINUS_EQUAL, minus);
        AssignmentOperators.registerAssignmentOperator(Token.MULTIPLY_EQUAL, multiply);
        AssignmentOperators.registerAssignmentOperator(Token.DIVIDE_EQUAL, divide);
        AssignmentOperators.registerAssignmentOperator(Token.MODULUS_EQUAL, modulus);
        AssignmentOperators.registerAssignmentOperator(Token.BITWISE_OR_EQUAL, bitwiseOr);
        AssignmentOperators.registerAssignmentOperator(Token.BITWISE_XOR_EQUAL, bitwiseXor);
        AssignmentOperators.registerAssignmentOperator(Token.BITWISE_AND_EQUAL, bitwiseAnd);
        AssignmentOperators.registerAssignmentOperator(Token.BIT_SHIFT_LEFT_EQUAL, bitShiftLeft);
        AssignmentOperators.registerAssignmentOperator(Token.BIT_SHIFT_RIGHT_EQUAL, bitShiftRight);
        AssignmentOperators.registerAssignmentOperator(Token.BIT_SHIFT_LOGICAL_RIGHT_EQUAL, bitShiftRightLogical);
        
        // Unary operators
        new UnaryOperator(Token.PLUS) {
            
            @Override
            public TYObject operate(TYObject value) {
                
                if (TrinityNatives.isInstance(value, "Trinity.Long")) {
                    
                    long thisLong = TrinityNatives.toLong(value);
                    return new TYLong(+thisLong);
                    
                } else if (TrinityNatives.isInstance(value, "Trinity.Float")) {
                    
                    double thisDouble = TrinityNatives.toFloat(value);
                    return new TYFloat(+thisDouble);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(value);
                    return new TYInt(+thisInt);
                }
            }
        };
        new UnaryOperator(Token.MINUS) {
            
            @Override
            public TYObject operate(TYObject value) {
                
                if (TrinityNatives.isInstance(value, "Trinity.Long")) {
                    
                    long thisLong = TrinityNatives.toLong(value);
                    return new TYLong(-thisLong);
                    
                } else if (TrinityNatives.isInstance(value, "Trinity.Float")) {
                    
                    double thisDouble = TrinityNatives.toFloat(value);
                    return new TYFloat(-thisDouble);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(value);
                    return new TYInt(-thisInt);
                }
            }
        };
        new UnaryOperator(Token.BITWISE_COMPLEMENT) {
            
            @Override
            public TYObject operate(TYObject value) {
                
                if (TrinityNatives.isInstance(value, "Trinity.Long")) {
                    
                    long thisLong = TrinityNatives.toLong(value);
                    return new TYLong(~thisLong);
                    
                } else {
                    
                    int thisInt = TrinityNatives.toInt(value);
                    return new TYInt(~thisInt);
                }
            }
        };
        new UnaryOperator(Token.NEGATIVE_OPERATOR) {
            
            @Override
            public TYObject operate(TYObject value) {
                
                return TYBoolean.valueFor(!TrinityNatives.toBoolean(value));
            }
        };
    }
}
