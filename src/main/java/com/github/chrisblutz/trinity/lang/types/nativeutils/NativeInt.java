package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeInt {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Int", "+", false, new String[]{"other"}, null, null, getActionForOperation("+"));
        TrinityNatives.registerMethod("Trinity.Int", "-", false, new String[]{"other"}, null, null, getActionForOperation("-"));
        TrinityNatives.registerMethod("Trinity.Int", "*", false, new String[]{"other"}, null, null, getActionForOperation("*"));
        TrinityNatives.registerMethod("Trinity.Int", "/", false, new String[]{"other"}, null, null, getActionForOperation("/"));
        TrinityNatives.registerMethod("Trinity.Int", "%", false, new String[]{"other"}, null, null, getActionForOperation("%"));
        TrinityNatives.registerMethod("Trinity.Int", "toString", false, null, null, null, (runtime, thisObj, params) -> new TYString(Integer.toString(TrinityNatives.toInt(thisObj))));
        TrinityNatives.registerMethod("Trinity.Int", "toHexString", false, null, null, null, (runtime, thisObj, params) -> new TYString(Integer.toHexString(TrinityNatives.toInt(thisObj))));
        TrinityNatives.registerMethod("Trinity.Int", "compareTo", false, new String[]{"other"}, null, null, (runtime, thisObj, params) -> {
            
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
                
                Errors.throwError("Trinity.Errors.InvalidTypeError", "Cannot compare types " + thisObj.getObjectClass().getName() + " and " + obj.getObjectClass().getName() + ".", runtime);
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod("Trinity.Int", "==", false, new String[]{"other"}, null, null, (runtime, thisObj, params) -> {
            
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
    
    private static boolean overflow = false;
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, thisObj, params) -> {
            
            int thisInt = TrinityNatives.toInt(thisObj);
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                
                if (!operation.contentEquals("/") || thisInt % newInt == 0) {
                    
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
                
                Errors.throwError("Trinity.Errors.InvalidTypeError", "Invalid type passed to '" + operation + "'.", runtime);
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
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
                    
                    return Math.floorDiv(int1, int2);
                
                case "%":
                    
                    return Math.floorMod(int1, int2);
                
                default:
                    
                    Errors.throwError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.");
                    
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
                
                return int1 / double1;
            
            case "%":
                
                return int1 % double1;
            
            default:
                
                Errors.throwError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.");
                
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
                
                return int1 / long1;
            
            case "%":
                
                return int1 % long1;
            
            default:
                
                Errors.throwError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.");
                
                return (long) int1;
        }
    }
}
