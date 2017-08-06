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
class NativeFloat {
    
    static void register() {
        
        TrinityNatives.registerField("Trinity.Float", "NaN", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.NaN));
        TrinityNatives.registerField("Trinity.Float", "POSITIVE_INFINITY", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.POSITIVE_INFINITY));
        TrinityNatives.registerField("Trinity.Float", "NEGATIVE_INFINITY", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.NEGATIVE_INFINITY));
        TrinityNatives.registerField("Trinity.Float", "MIN_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.MIN_VALUE));
        TrinityNatives.registerField("Trinity.Float", "MAX_VALUE", (runtime, thisObj, params) -> TrinityNatives.wrapNumber(Double.MAX_VALUE));
        
        TrinityNatives.registerMethod("Trinity.Float", "+", false, new String[]{"other"}, null, null, null, getActionForOperation("+"));
        TrinityNatives.registerMethod("Trinity.Float", "-", false, new String[]{"other"}, null, null, null, getActionForOperation("-"));
        TrinityNatives.registerMethod("Trinity.Float", "*", false, new String[]{"other"}, null, null, null, getActionForOperation("*"));
        TrinityNatives.registerMethod("Trinity.Float", "/", false, new String[]{"other"}, null, null, null, getActionForOperation("/"));
        TrinityNatives.registerMethod("Trinity.Float", "%", false, new String[]{"other"}, null, null, null, getActionForOperation("%"));
        TrinityNatives.registerMethod("Trinity.Float", "toString", false, null, null, null, null, (runtime, thisObj, params) -> new TYString(Double.toString(TrinityNatives.toFloat(thisObj))));
        TrinityNatives.registerMethod("Trinity.Float", "compareTo", false, new String[]{"other"}, null, null, null, (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
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
                
                Errors.throwError("Trinity.Errors.InvalidTypeError", runtime, "Cannot compare types " + thisObj.getObjectClass().getName() + " and " + obj.getObjectClass().getName() + ".");
            }
            
            return new TYInt(-1);
        });
        TrinityNatives.registerMethod("Trinity.Float", "==", false, new String[]{"other"}, null, null, null, (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
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
        TrinityNatives.registerMethod("Trinity.Float", "isNaN", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            return TYBoolean.valueFor(Double.isNaN(thisDouble));
        });
        TrinityNatives.registerMethod("Trinity.Float", "isFinite", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            return TYBoolean.valueFor(Double.isFinite(thisDouble));
        });
        TrinityNatives.registerMethod("Trinity.Float", "isInfinite", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            return TYBoolean.valueFor(Double.isInfinite(thisDouble));
        });
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.toFloat(thisObj);
            
            TYObject returnVal;
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYInt) {
                
                int newInt = ((TYInt) obj).getInternalInteger();
                returnVal = new TYFloat(doubleCalculation(thisDouble, newInt, operation));
                
            } else if (obj instanceof TYLong) {
                
                long newLong = ((TYLong) obj).getInternalLong();
                returnVal = new TYFloat(doubleCalculation(thisDouble, newLong, operation));
                
            } else if (obj instanceof TYFloat) {
                
                double newDouble = ((TYFloat) obj).getInternalDouble();
                returnVal = new TYFloat(doubleCalculation(thisDouble, newDouble, operation));
                
            } else {
                
                Errors.throwError("Trinity.Errors.InvalidTypeError", runtime, "Invalid type passed to '" + operation + "'.");
                
                returnVal = TYObject.NONE;
            }
            
            return returnVal;
        };
    }
    
    private static double doubleCalculation(double double1, int int1, String operation) {
        
        switch (operation) {
            
            case "+":
                
                return double1 + int1;
            
            case "-":
                
                return double1 - int1;
            
            case "*":
                
                return double1 * int1;
            
            case "/":
                
                NumericHelper.checkDivision(double1, int1);
                
                return double1 / int1;
            
            case "%":
                
                return double1 % int1;
            
            default:
                
                Errors.throwError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.");
                
                return double1;
        }
    }
    
    private static double doubleCalculation(double double1, double double2, String operation) {
        
        switch (operation) {
            
            case "+":
                
                return double1 + double2;
            
            case "-":
                
                return double1 - double2;
            
            case "*":
                
                return double1 * double2;
            
            case "/":
                
                NumericHelper.checkDivision(double1, double2);
                
                return double1 / double2;
            
            case "%":
                
                return double1 % double2;
            
            default:
                
                Errors.throwError("Trinity.Errors.UnsupportedOperationError", "Operation '" + operation + "' not supported.");
                
                return double1;
        }
    }
}
