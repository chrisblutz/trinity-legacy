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
class NativeLong {
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.LONG, "MIN_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Long.MIN_VALUE));
        TrinityNatives.registerField(TrinityNatives.Classes.LONG, "MAX_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Long.MAX_VALUE));
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "+", getActionForOperation("+"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "-", getActionForOperation("-"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "*", getActionForOperation("*"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "/", getActionForOperation("/"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "%", getActionForOperation("%"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "<<", (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            return new TYLong(TrinityNatives.toLong(thisObj) << TrinityNatives.toLong(other));
            
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, ">>", (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            return new TYLong(TrinityNatives.toLong(thisObj) >> TrinityNatives.toLong(other));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, ">>>", (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            return new TYLong(TrinityNatives.toLong(thisObj) >>> TrinityNatives.toLong(other));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "toString", (runtime, thisObj, params) -> new TYString(Long.toString(TrinityNatives.toLong(thisObj))));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "toHexString", (runtime, thisObj, params) -> new TYString(Long.toHexString(TrinityNatives.toLong(thisObj))));
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "compareTo", (runtime, thisObj, params) -> {
            
            long thisLong = TrinityNatives.toLong(thisObj);
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return new TYInt(Long.compare(thisLong, objInt));
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return new TYInt(Long.compare(thisLong, objLong));
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return new TYInt(Double.compare(thisLong, objDouble));
                
            } else {
                
                Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, runtime, "Cannot compare types " + thisObj.getObjectClass().getName() + " and " + obj.getObjectClass().getName() + ".");
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.LONG, "==", (runtime, thisObj, params) -> {
            
            long thisLong = TrinityNatives.toLong(thisObj);
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return TYBoolean.valueFor(thisLong == objInt);
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return TYBoolean.valueFor(thisLong == objLong);
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return TYBoolean.valueFor(thisLong == objDouble);
            }
            
            return TYBoolean.FALSE;
        });
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, thisObj, params) -> {
            
            long thisLong = TrinityNatives.toLong(thisObj);
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                
                if (!operation.contentEquals("/") || thisLong % newInt == 0) {
                    
                    returnVal = new TYLong(longCalculation(thisLong, newInt, operation));
                    
                } else {
                    
                    double result = doubleCalculation(thisLong, newInt, operation);
                    returnVal = new TYFloat(result);
                }
                
            } else if (obj instanceof TYLong) {
                
                long newLong = ((TYLong) obj).getInternalLong();
                
                if (!operation.contentEquals("/") || thisLong % newLong == 0) {
                    
                    returnVal = new TYLong(longCalculation(thisLong, newLong, operation));
                    
                } else {
                    
                    double result = doubleCalculation(thisLong, newLong, operation);
                    returnVal = new TYFloat(result);
                }
                
            } else if (obj instanceof TYFloat) {
                
                double newDouble = ((TYFloat) obj).getInternalDouble();
                returnVal = new TYFloat(doubleCalculation(thisLong, newDouble, operation));
                
            } else {
                
                Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, runtime, "Invalid type passed to '" + operation + "'.");
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
    }
    
    private static long longCalculation(long long1, long long2, String operation) {
        
        switch (operation) {
            
            case "+":
                
                return long1 + long2;
            
            case "-":
                
                return long1 - long2;
            
            case "*":
                
                return long1 * long2;
            
            case "/":
                
                NumericHelper.checkDivision(long1, long2);
                
                return long1 / long2;
            
            case "%":
                
                return long1 % long2;
            
            default:
                
                Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, "Operation '" + operation + "' not supported.");
                
                return long1;
        }
    }
    
    private static double doubleCalculation(long long1, double double1, String operation) {
        
        switch (operation) {
            
            case "+":
                
                return long1 + double1;
            
            case "-":
                
                return long1 - double1;
            
            case "*":
                
                return long1 * double1;
            
            case "/":
                
                NumericHelper.checkDivision(long1, double1);
                
                return long1 / double1;
            
            case "%":
                
                return long1 % double1;
            
            default:
                
                Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, "Operation '" + operation + "' not supported.");
                
                return (double) long1;
        }
    }
}
