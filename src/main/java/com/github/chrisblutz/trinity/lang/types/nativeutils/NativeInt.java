package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
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
        
        TrinityNatives.registerMethod("Int", "+", false, new String[]{"other"}, null, null, getActionForOperation("+"));
        TrinityNatives.registerMethod("Int", "-", false, new String[]{"other"}, null, null, getActionForOperation("-"));
        TrinityNatives.registerMethod("Int", "*", false, new String[]{"other"}, null, null, getActionForOperation("*"));
        TrinityNatives.registerMethod("Int", "/", false, new String[]{"other"}, null, null, getActionForOperation("/"));
        TrinityNatives.registerMethod("Int", "%", false, new String[]{"other"}, null, null, getActionForOperation("%"));
        TrinityNatives.registerMethod("Int", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(Integer.toString(((TYInt) thisObj).getInternalInteger())));
        TrinityNatives.registerMethod("Int", "toHexString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(Integer.toHexString(((TYInt) thisObj).getInternalInteger())));
        TrinityNatives.registerMethod("Int", "compareTo", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            int thisInt = ((TYInt) thisObj).getInternalInteger();
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
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod("Int", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            int thisInt = ((TYInt) thisObj).getInternalInteger();
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return new TYBoolean(thisInt == objInt);
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return new TYBoolean(thisInt == objLong);
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return new TYBoolean(thisInt == objDouble);
            }
            
            return TYBoolean.FALSE;
        });
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            int thisInt = ((TYInt) thisObj).getInternalInteger();
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                returnVal = new TYInt(intCalculation(thisInt, newInt, operation, stackTrace));
                
            } else if (obj instanceof TYFloat) {
                
                double newDouble = ((TYFloat) obj).getInternalDouble();
                returnVal = new TYFloat(doubleCalculation(thisInt, newDouble, operation, stackTrace));
                
            } else if (obj instanceof TYLong) {
                
                long newLong = ((TYLong) obj).getInternalLong();
                returnVal = new TYLong(longCalculation(thisInt, newLong, operation, stackTrace));
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Invalid type passed to '" + operation + "'.", stackTrace);
                error.throwError();
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
    }
    
    private static int intCalculation(int int1, int int2, String operation, TYStackTrace stackTrace) {
        
        switch (operation) {
            
            case "+":
                
                return int1 + int2;
            
            case "-":
                
                return int1 - int2;
            
            case "*":
                
                return int1 * int2;
            
            case "/":
                
                return int1 / int2;
            
            case "%":
                
                return int1 % int2;
            
            default:
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return int1;
        }
    }
    
    private static double doubleCalculation(int int1, double double1, String operation, TYStackTrace stackTrace) {
        
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
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return (double) int1;
        }
    }
    
    private static long longCalculation(int int1, long long1, String operation, TYStackTrace stackTrace) {
        
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
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return (long) int1;
        }
    }
}
