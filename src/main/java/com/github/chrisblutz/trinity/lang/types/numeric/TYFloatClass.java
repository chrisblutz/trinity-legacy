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
public class TYFloatClass extends TYClass {
    
    public TYFloatClass() {
        
        super("Float", "Float", null);
        
        registerMethod(new TYMethod("+", false, null, new TYProcedure(getActionForOperation("+"))));
        registerMethod(new TYMethod("-", false, null, new TYProcedure(getActionForOperation("-"))));
        registerMethod(new TYMethod("*", false, null, new TYProcedure(getActionForOperation("*"))));
        registerMethod(new TYMethod("/", false, null, new TYProcedure(getActionForOperation("/"))));
        registerMethod(new TYMethod("%", false, null, new TYProcedure(getActionForOperation("%"))));
        registerMethod(new TYMethod("toString", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Double.toString(((TYFloat) thisObj).getInternalDouble())))));
        registerMethod(new TYMethod("compareTo", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            double thisDouble = ((TYFloat) thisObj).getInternalDouble();
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
                if (obj instanceof TYInt) {
                    
                    int objInt = ((TYInt) obj).getInternalInteger();
                    
                    return new TYInt(Double.compare(thisDouble, objInt));
                    
                } else if (obj instanceof TYLong) {
                    
                    long objLong = ((TYLong) obj).getInternalLong();
                    
                    return new TYInt(Double.compare(thisDouble, objLong));
                    
                } else if (obj instanceof TYFloat) {
                    
                    double objDouble = ((TYFloat) obj).getInternalDouble();
                    
                    return new TYInt(Double.compare(thisDouble, objDouble));
                }
            }
            
            return new TYInt(-1);
        })));
        registerMethod(new TYMethod("==", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            double thisDouble = ((TYFloat) thisObj).getInternalDouble();
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
                if (obj instanceof TYInt) {
                    
                    int objInt = ((TYInt) obj).getInternalInteger();
                    
                    return new TYBoolean(thisDouble == objInt);
                    
                } else if (obj instanceof TYLong) {
                    
                    long objLong = ((TYLong) obj).getInternalLong();
                    
                    return new TYBoolean(thisDouble == objLong);
                    
                } else if (obj instanceof TYFloat) {
                    
                    double objDouble = ((TYFloat) obj).getInternalDouble();
                    
                    return new TYBoolean(thisDouble == objDouble);
                }
            }
            
            return TYBoolean.FALSE;
        })));
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            double thisDouble = ((TYFloat) thisObj).getInternalDouble();
            
            TYObject returnVal;
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                
                if (obj instanceof TYInt) {
                    
                    int newInt = ((TYInt) obj).getInternalInteger();
                    returnVal = new TYFloat(doubleCalculation(thisDouble, newInt, operation, stackTrace));
                    
                } else if (obj instanceof TYFloat) {
                    
                    double newDouble = ((TYFloat) obj).getInternalDouble();
                    returnVal = new TYFloat(doubleCalculation(thisDouble, newDouble, operation, stackTrace));
                    
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
                
                TYError error = new TYError(new TYUnsupportedOperationError(), "Operation '" + operation + "' not supported.", stackTrace);
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
                
                TYError error = new TYError(new TYUnsupportedOperationError(), "Operation '" + operation + "' not supported.", stackTrace);
                error.throwError();
                
                return double1;
        }
    }
}
