package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeLong {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Long.+", new TYMethod("+", false, new TYProcedure(getActionForOperation("+"))));
        methods.put("Long.-", new TYMethod("-", false, new TYProcedure(getActionForOperation("-"))));
        methods.put("Long.*", new TYMethod("*", false, new TYProcedure(getActionForOperation("*"))));
        methods.put("Long./", new TYMethod("/", false, new TYProcedure(getActionForOperation("/"))));
        methods.put("Long.%", new TYMethod("%", false, new TYProcedure(getActionForOperation("%"))));
        methods.put("Long.toString", new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Long.toString(((TYLong) thisObj).getInternalLong())))));
        methods.put("Long.toHexString", new TYMethod("toHexString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Long.toHexString(((TYLong) thisObj).getInternalLong())))));
        methods.put("Long.compareTo", new TYMethod("compareTo", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            long thisLong = ((TYLong) thisObj).getInternalLong();
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
                if (obj instanceof TYInt) {
                    
                    int objInt = ((TYInt) obj).getInternalInteger();
                    
                    return new TYInt(Long.compare(thisLong, objInt));
                    
                } else if (obj instanceof TYLong) {
                    
                    long objLong = ((TYLong) obj).getInternalLong();
                    
                    return new TYInt(Long.compare(thisLong, objLong));
                    
                } else if (obj instanceof TYFloat) {
                    
                    double objDouble = ((TYFloat) obj).getInternalDouble();
                    
                    return new TYInt(Double.compare(thisLong, objDouble));
                }
            }
            
            return new TYInt(-1);
        })));
        methods.put("Long.==", new TYMethod("==", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            long thisLong = ((TYLong) thisObj).getInternalLong();
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
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
            }
            
            return TYBoolean.FALSE;
        })));
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            long thisLong = ((TYLong) thisObj).getInternalLong();
            
            TYObject returnVal;
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                
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
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "'" + operation + "' requires two operands.", stackTrace);
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
