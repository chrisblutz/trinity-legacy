package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeNumeric {
    
    private static List<String> prohibitedFloatOperators = new ArrayList<>(Arrays.asList("<<", ">>", ">>>"));
    
    protected static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "+", getAction("+"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "-", getAction("-"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "*", getAction("*"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "/", getAction("/"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "%", getAction("%"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "<<", getAction("<<"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, ">>", getAction(">>"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, ">>>", getAction(">>>"));
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "toString", (runtime, thisObj, params) -> {
            
            String string = "";
            if (TrinityNatives.isInstance(thisObj, TrinityNatives.Classes.INT)) {
                
                string = Integer.toString(TrinityNatives.toInt(thisObj));
                
            } else if (TrinityNatives.isInstance(thisObj, TrinityNatives.Classes.LONG)) {
                
                string = Long.toString(TrinityNatives.toLong(thisObj));
                
            } else if (TrinityNatives.isInstance(thisObj, TrinityNatives.Classes.FLOAT)) {
                
                string = Double.toString(TrinityNatives.toFloat(thisObj));
                
            } else {
                
                Errors.throwError(Errors.Classes.NOT_IMPLEMENTED_ERROR, runtime, "String conversion not defined for type " + thisObj.getObjectClass().getName() + ".");
            }
            
            return new TYString(string);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "toHexString", (runtime, thisObj, params) -> {
            
            String string = "";
            if (TrinityNatives.isInstance(thisObj, TrinityNatives.Classes.INT)) {
                
                string = Integer.toHexString(TrinityNatives.toInt(thisObj));
                
            } else if (TrinityNatives.isInstance(thisObj, TrinityNatives.Classes.LONG)) {
                
                string = Long.toHexString(TrinityNatives.toLong(thisObj));
                
            } else if (TrinityNatives.isInstance(thisObj, TrinityNatives.Classes.FLOAT)) {
                
                string = Double.toHexString(TrinityNatives.toFloat(thisObj));
                
            } else {
                
                Errors.throwError(Errors.Classes.NOT_IMPLEMENTED_ERROR, runtime, "Hexadecimal string conversion not defined for type " + thisObj.getObjectClass().getName() + ".");
            }
            
            return new TYString(string);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "compareTo", (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.asNumber(thisObj);
            double otherDouble = TrinityNatives.asNumber(runtime.getVariable("other"));
            
            return new TYInt(Double.compare(thisDouble, otherDouble));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.NUMERIC, "==", (runtime, thisObj, params) -> {
            
            double thisDouble = TrinityNatives.asNumber(thisObj);
            double otherDouble = TrinityNatives.asNumber(runtime.getVariable("other"));
            
            return TYBoolean.valueFor(thisDouble == otherDouble);
        });
    }
    
    private static ProcedureAction getAction(final String operation) {
        
        return (runtime, thisObj, params) -> {
            
            TYObject other = runtime.getVariable("other");
            
            double thisDouble = TrinityNatives.asNumber(thisObj);
            double otherDouble = TrinityNatives.asNumber(other);
            
            // Perform check for unsupported operations on floating-point values
            if (prohibitedFloatOperators.contains(operation) && (thisDouble % 1 != 0 || otherDouble % 1 != 0)) {
                
                Errors.throwError(Errors.Classes.UNSUPPORTED_OPERATION_ERROR, runtime, "Operation " + operation + " not supported on non-integer values.");
            }
            
            return TrinityNatives.wrapNumber(performOperation(operation, thisDouble, otherDouble));
        };
    }
    
    private static double performOperation(String operation, double thisDouble, double otherDouble) {
        
        switch (operation) {
            
            case "+":
                
                return thisDouble + otherDouble;
            
            case "-":
                
                return thisDouble - otherDouble;
            
            case "*":
                
                return thisDouble * otherDouble;
            
            case "/":
                
                // Check for / by 0
                checkDivision(otherDouble);
                
                return thisDouble / otherDouble;
            
            case "%":
                
                // Check for / by 0
                checkDivision(otherDouble);
                
                if (thisDouble % 1 == 0 && otherDouble % 1 == 0) {
                    
                    return Math.floorMod((long) thisDouble, (long) otherDouble);
                    
                } else {
                    
                    return thisDouble % otherDouble;
                }
            
            case "<<":
                
                return (long) thisDouble << (long) otherDouble;
            
            case ">>":
                
                return (long) thisDouble >> (long) otherDouble;
            
            case ">>>":
                
                return (long) thisDouble >>> (long) otherDouble;
        }
        
        return 0;
    }
    
    private static void checkDivision(double denominator) {
        
        if (denominator == 0) {
            
            Errors.throwError(Errors.Classes.ARITHMETIC_ERROR, "/ by 0.");
        }
    }
}
