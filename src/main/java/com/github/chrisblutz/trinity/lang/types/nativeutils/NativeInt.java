package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.NumericHelper;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeInt {
    
    private static boolean overflow = false;
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.INT, "MIN_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Integer.MIN_VALUE));
        TrinityNatives.registerField(TrinityNatives.Classes.INT, "MAX_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Integer.MAX_VALUE));
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "+", getActionForOperation("+"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "-", getActionForOperation("-"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "*", getActionForOperation("*"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "/", getActionForOperation("/"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "%", getActionForOperation("%"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "<<", (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            if (TrinityNatives.isInstance(other, TrinityNatives.Classes.LONG)) {
                
                return new TYLong(TrinityNatives.toLong(thisObj) << TrinityNatives.toLong(other));
                
            } else {
                
                return new TYInt(TrinityNatives.toInt(thisObj) << TrinityNatives.toInt(other));
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, ">>", (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            if (TrinityNatives.isInstance(other, TrinityNatives.Classes.LONG)) {
                
                return new TYLong(TrinityNatives.toLong(thisObj) >> TrinityNatives.toLong(other));
                
            } else {
                
                return new TYInt(TrinityNatives.toInt(thisObj) >> TrinityNatives.toInt(other));
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, ">>>", (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            if (TrinityNatives.isInstance(other, TrinityNatives.Classes.LONG)) {
                
                return new TYLong(TrinityNatives.toLong(thisObj) >>> TrinityNatives.toLong(other));
                
            } else {
                
                return new TYInt(TrinityNatives.toInt(thisObj) >>> TrinityNatives.toInt(other));
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "toString", (runtime, thisObj, params) -> new TYString(Integer.toString(TrinityNatives.toInt(thisObj))));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "toHexString", (runtime, thisObj, params) -> new TYString(Integer.toHexString(TrinityNatives.toInt(thisObj))));
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "compareTo", (runtime, thisObj, params) -> {
            
            int thisInt = TrinityNatives.toInt(thisObj);
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return new TYInt(Integer.compare(thisInt, objInt));
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return new TYInt(Long.compare(thisInt, objLong));
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return new TYInt(Double.compare(thisInt, objDouble));
                
            } else {
                
                Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, runtime, "Cannot compare types " + thisObj.getObjectClass().getName() + " and " + obj.getObjectClass().getName() + ".");
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.INT, "==", (runtime, thisObj, params) -> {
            
            int thisInt = TrinityNatives.toInt(thisObj);
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return TYBoolean.valueFor(thisInt == objInt);
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return TYBoolean.valueFor(thisInt == objLong);
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return TYBoolean.valueFor(thisInt == objDouble);
            }
            
            return TYBoolean.FALSE;
        });
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, thisObj, params) -> {
            
            int thisInt = TrinityNatives.toInt(thisObj);
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                
                if (!operation.contentEquals("/") || modulusZero(thisInt, newInt)) {
                    
                    long result = intCalculation(thisInt, newInt, operation);
                    
                    if (overflow) {
                        
                        overflow = false;
                        returnVal = new TYLong(result);
                        
                    } else {
                        
                        returnVal = new TYInt((int) result);
                    }
                    
                } else {
                    
                    double result = doubleCalculation(thisInt, newInt, operation);
                    returnVal = new TYFloat(result);
                }
                
            } else if (obj instanceof TYFloat) {
                
                double newDouble = ((TYFloat) obj).getInternalDouble();
                returnVal = new TYFloat(doubleCalculation(thisInt, newDouble, operation));
                
            } else if (obj instanceof TYLong) {
                
                long newLong = ((TYLong) obj).getInternalLong();
                
                if (!operation.contentEquals("/") || thisInt % newLong == 0) {
                    
                    returnVal = new TYLong(longCalculation(thisInt, newLong, operation));
                    
                } else {
                    
                    double result = doubleCalculation(thisInt, newLong, operation);
                    returnVal = new TYFloat(result);
                }
                
            } else {
                
                Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, runtime, "Invalid type passed to '" + operation + "'.");
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
    }
    
    private static boolean modulusZero(int thisInt, int newInt) {
        
        NumericHelper.checkDivision(thisInt, newInt);
        
        return thisInt % newInt == 0;
    }
    
    private static long intCalculation(int int1, int int2, String operation) {
        
        try {
            
            switch (operation) {
                
                case "+":
                    
                    return Math.addExact(int1, int2);
                
                case "-":
                    
                    return Math.subtractExact(int1, int2);
                
                case "*":
                    
                    return Math.multiplyExact(int1, int2);
                
                case "/":
                    
                    NumericHelper.checkDivision(int1, int2);
                    
                    return Math.floorDiv(int1, int2);
                
                case "%":
                    
                    return Math.floorMod(int1, int2);
                
                default:
                    
                    Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, "Operation '" + operation + "' not supported.");
                    
                    return int1;
            }
            
        } catch (ArithmeticException e) {
            
            overflow = true;
            
            return longCalculation(int1, int2, operation);
        }
    }
    
    private static double doubleCalculation(int int1, double double1, String operation) {
        
        switch (operation) {
            
            case "+":
                
                return int1 + double1;
            
            case "-":
                
                return int1 - double1;
            
            case "*":
                
                return int1 * double1;
            
            case "/":
                
                NumericHelper.checkDivision(int1, double1);
                
                return int1 / double1;
            
            case "%":
                
                return int1 % double1;
            
            default:
                
                Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, "Operation '" + operation + "' not supported.");
                
                return (double) int1;
        }
    }
    
    private static long longCalculation(int int1, long long1, String operation) {
        
        switch (operation) {
            
            case "+":
                
                return int1 + long1;
            
            case "-":
                
                return int1 - long1;
            
            case "*":
                
                return int1 * long1;
            
            case "/":
                
                NumericHelper.checkDivision(int1, long1);
                
                return int1 / long1;
            
            case "%":
                
                return int1 % long1;
            
            default:
                
                Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, "Operation '" + operation + "' not supported.");
                
                return (long) int1;
        }
    }
}
