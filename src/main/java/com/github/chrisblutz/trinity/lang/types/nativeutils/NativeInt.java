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
class NativeInt {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Int.+", new TYMethod("+", false, new TYProcedure(getActionForOperation("+"))));
        methods.put("Int.-", new TYMethod("-", false, new TYProcedure(getActionForOperation("-"))));
        methods.put("Int.*", new TYMethod("*", false, new TYProcedure(getActionForOperation("*"))));
        methods.put("Int./", new TYMethod("/", false, new TYProcedure(getActionForOperation("/"))));
        methods.put("Int.%", new TYMethod("%", false, new TYProcedure(getActionForOperation("%"))));
        methods.put("Int.toString", new TYMethod("toString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Integer.toString(((TYInt) thisObj).getInternalInteger())))));
        methods.put("Int.toHexString", new TYMethod("toHexString", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYString(Integer.toHexString(((TYInt) thisObj).getInternalInteger())))));
        methods.put("Int.compareTo", new TYMethod("compareTo", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            int thisInt = ((TYInt) thisObj).getInternalInteger();
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
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
            }
            
            return new TYInt(-1);
        })));
        methods.put("Int.==", new TYMethod("==", false, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            int thisInt = ((TYInt) thisObj).getInternalInteger();
            
            if (params.length > 0) {
                
                TYObject obj = params[0];
                
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
            }
            
            return TYBoolean.FALSE;
        })));
    }
    
    private static ProcedureAction getActionForOperation(String operation) {
        
        return (runtime, stackTrace, thisObj, params) -> {
            
            int thisInt = ((TYInt) thisObj).getInternalInteger();
            
            TYObject returnVal;
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                
                if (obj instanceof TYInt) {
                    
                    int newInt = ((TYInt) obj).getInternalInteger();
                    returnVal = new TYInt(intCalculation(thisInt, newInt, operation, stackTrace));
                    
                } else if (obj instanceof TYFloat) {
                    
                    double newDouble = ((TYFloat) obj).getInternalDouble();
                    returnVal = new TYFloat(doubleCalculation(thisInt, newDouble, operation, stackTrace));
                    
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
}
