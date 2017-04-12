package com.github.chrisblutz.trinity.lang.types.numeric;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidArgumentNumberError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidTypeError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYUnsupportedOperationError;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;


/**
 * @author Christopher Lutz
 */
public class TYLongClass extends TYClass {
    
    public TYLongClass() {
        
        super("Long", "Long", null);
        
        registerMethod(new TYMethod("+", false, null, new TYProcedure(getActionForOperation("+"))));
        registerMethod(new TYMethod("-", false, null, new TYProcedure(getActionForOperation("-"))));
        registerMethod(new TYMethod("*", false, null, new TYProcedure(getActionForOperation("*"))));
        registerMethod(new TYMethod("/", false, null, new TYProcedure(getActionForOperation("/"))));
        registerMethod(new TYMethod("%", false, null, new TYProcedure(getActionForOperation("%"))));
        registerMethod(new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Long.toString(((TYLong) thisObj).getInternalLong())))));
        registerMethod(new TYMethod("toHexString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Long.toHexString(((TYLong) thisObj).getInternalLong())))));
        registerMethod(new TYMethod("compareTo", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
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
        registerMethod(new TYMethod("==", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
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
                    
                    TYError error = new TYError(new TYInvalidTypeError(), "Invalid type passed to '" + operation + "'.", stackTrace);
                    error.throwError();
                    
                    returnVal = TYObject.NONE;
                }
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "'" + operation + "' requires two operands.", stackTrace);
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
                
                TYError error = new TYError(new TYUnsupportedOperationError(), "Operation '" + operation + "' not supported.", stackTrace);
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
                
                TYError error = new TYError(new TYUnsupportedOperationError(), "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return (double) long1;
        }
    }
}
