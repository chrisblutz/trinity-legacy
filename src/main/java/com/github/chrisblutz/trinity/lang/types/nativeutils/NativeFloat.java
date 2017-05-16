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
class NativeFloat {
    
    static void register() {
        
        TrinityNatives.registerMethod("Float", "+", false, new String[]{"other"}, null, null, getActionForOperation("+"));
        TrinityNatives.registerMethod("Float", "-", false, new String[]{"other"}, null, null, getActionForOperation("-"));
        TrinityNatives.registerMethod("Float", "*", false, new String[]{"other"}, null, null, getActionForOperation("*"));
        TrinityNatives.registerMethod("Float", "/", false, new String[]{"other"}, null, null, getActionForOperation("/"));
        TrinityNatives.registerMethod("Float", "%", false, new String[]{"other"}, null, null, getActionForOperation("%"));
        TrinityNatives.registerMethod("Float", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYString(Double.toString(TrinityNatives.cast(TYFloat.class, thisObj, stackTrace).getInternalDouble())));
        TrinityNatives.registerMethod("Float", "compareTo", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.cast(TYFloat.class, thisObj, stackTrace).getInternalDouble();
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return new TYInt(Double.compare(thisDouble, objInt));
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return new TYInt(Double.compare(thisDouble, objLong));
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return new TYInt(Double.compare(thisDouble, objDouble));
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Cannot compare types " + thisObj.getObjectClass().getName() + " and " + obj.getObjectClass().getName() + ".", stackTrace);
                error.throwError();
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod("Float", "==", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.cast(TYFloat.class, thisObj, stackTrace).getInternalDouble();
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int objInt = ((TYInt) obj).getInternalInteger();
                
                return TYBoolean.valueFor(thisDouble == objInt);
                
            } else if (obj instanceof TYLong) {
                
                long objLong = ((TYLong) obj).getInternalLong();
                
                return TYBoolean.valueFor(thisDouble == objLong);
                
            } else if (obj instanceof TYFloat) {
                
                double objDouble = ((TYFloat) obj).getInternalDouble();
                
                return TYBoolean.valueFor(thisDouble == objDouble);
            }
            
            return TYBoolean.FALSE;
        });
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.cast(TYFloat.class, thisObj, stackTrace).getInternalDouble();
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                returnVal = new TYFloat(doubleCalculation(thisDouble, newInt, operation, stackTrace));
                
            } else if (obj instanceof TYLong) {
                
                long newLong = ((TYLong) obj).getInternalLong();
                returnVal = new TYFloat(doubleCalculation(thisDouble, newLong, operation, stackTrace));
                
            } else if (obj instanceof TYFloat) {
                
                double newDouble = ((TYFloat) obj).getInternalDouble();
                returnVal = new TYFloat(doubleCalculation(thisDouble, newDouble, operation, stackTrace));
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Invalid type passed to '" + operation + "'.", stackTrace);
                error.throwError();
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
    }
    
    private static double doubleCalculation(double double1, int int1, String operation, TYStackTrace stackTrace) {
        
        switch (operation) {
            
            case "+":
                
                return double1 + int1;
            
            case "-":
                
                return double1 - int1;
            
            case "*":
                
                return double1 * int1;
            
            case "/":
                
                return double1 / int1;
            
            case "%":
                
                return double1 % int1;
            
            default:
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return double1;
        }
    }
    
    private static double doubleCalculation(double double1, double double2, String operation, TYStackTrace stackTrace) {
        
        switch (operation) {
            
            case "+":
                
                return double1 + double2;
            
            case "-":
                
                return double1 - double2;
            
            case "*":
                
                return double1 * double2;
            
            case "/":
                
                return double1 / double2;
            
            case "%":
                
                return double1 % double2;
            
            default:
                
                TYError error = new TYError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return double1;
        }
    }
}
