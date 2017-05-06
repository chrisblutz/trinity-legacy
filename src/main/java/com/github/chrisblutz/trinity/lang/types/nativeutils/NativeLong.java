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
class NativeLong {
    
    static void register() {
        
        TrinityNatives.registerMethod("Long", "+", false, new String[]{"other"}, null, null, getActionForOperation("+"));
        TrinityNatives.registerMethod("Long", "-", false, new String[]{"other"}, null, null, getActionForOperation("-"));
        TrinityNatives.registerMethod("Long", "*", false, new String[]{"other"}, null, null, getActionForOperation("*"));
        TrinityNatives.registerMethod("Long", "/", false, new String[]{"other"}, null, null, getActionForOperation("/"));
        TrinityNatives.registerMethod("Long", "%", false, new String[]{"other"}, null, null, getActionForOperation("%"));
        TrinityNatives.registerMethod("Long", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(Long.toString(TrinityNatives.cast(TYLong.class, thisObj, stackTrace).getInternalLong())));
        TrinityNatives.registerMethod("Long", "toHexString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(Long.toHexString(TrinityNatives.cast(TYLong.class, thisObj, stackTrace).getInternalLong())));
        TrinityNatives.registerMethod("Long", "compareTo", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            long thisLong = TrinityNatives.cast(TYLong.class, thisObj, stackTrace).getInternalLong();
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
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Cannot compare types " + thisObj.getObjectClass().getName() + " and " + obj.getObjectClass().getName() + ".", stackTrace);
                error.throwError();
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod("Long", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            long thisLong = TrinityNatives.cast(TYLong.class, thisObj, stackTrace).getInternalLong();
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return new TYBoolean(thisLong == objInt);
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return new TYBoolean(thisLong == objLong);
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return new TYBoolean(thisLong == objDouble);
            }
            
            return TYBoolean.FALSE;
        });
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            long thisLong = TrinityNatives.cast(TYLong.class, thisObj, stackTrace).getInternalLong();
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                returnVal = new TYLong(longCalculation(thisLong, newInt, operation, stackTrace));
                
            } else if (obj instanceof TYLong) {
                
                long newLong = ((TYLong) obj).getInternalLong();
                returnVal = new TYLong(longCalculation(thisLong, newLong, operation, stackTrace));
                
            } else if (obj instanceof TYFloat) {
                
                double newDouble = ((TYFloat) obj).getInternalDouble();
                returnVal = new TYFloat(doubleCalculation(thisLong, newDouble, operation, stackTrace));
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Invalid type passed to '" + operation + "'.", stackTrace);
                error.throwError();
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
    }
    
    private static long longCalculation(long long1, long long2, String operation, TYStackTrace stackTrace) {
        
        switch (operation) {
            
            case "+":
                
                return long1 + long2;
            
            case "-":
                
                return long1 - long2;
            
            case "*":
                
                return long1 * long2;
            
            case "/":
                
                return long1 / long2;
            
            case "%":
                
                return long1 % long2;
            
            default:
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return long1;
        }
    }
    
    private static double doubleCalculation(long long1, double double1, String operation, TYStackTrace stackTrace) {
        
        switch (operation) {
            
            case "+":
                
                return long1 + double1;
            
            case "-":
                
                return long1 - double1;
            
            case "*":
                
                return long1 * double1;
            
            case "/":
                
                return long1 / double1;
            
            case "%":
                
                return long1 % double1;
            
            default:
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return (double) long1;
        }
    }
}
