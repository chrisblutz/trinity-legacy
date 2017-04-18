package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Map;
import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
class NativeKernel {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Kernel.print", new TYMethod("print", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                TYObject strObj = obj.tyInvoke("toString", runtime, stackTrace, null, null);
                
                if (strObj instanceof TYString) {
                    
                    System.out.print(((TYString) strObj).getInternalString());
                    
                } else if (strObj == null) {
                    
                    System.out.print("nil");
                }
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "Kernel.print receives one argument.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
        methods.put("Kernel.readln", new TYMethod("readln", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            Scanner sc = new Scanner(System.in);
            return new TYString(sc.nextLine());
        })));
        methods.put("Kernel.currentTimeMillis", new TYMethod("currentTimeMillis", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 0 || params[0] == TYObject.NONE) {
                
                return new TYLong(System.currentTimeMillis());
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "Kernel.currentTimeMillis takes no argument(s).", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
        methods.put("Kernel.throw", new TYMethod("throw", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0 && params[0].getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Errors.Error"))) {
                
                String errorMessage = ((TYString) params[0].tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString();
                System.err.println(errorMessage);
                
                Trinity.exit(1);
                
                // TODO allow for catching errors
            }
            
            return TYObject.NONE;
        })));
        methods.put("Kernel.exit", new TYMethod("exit", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                if (obj instanceof TYInt) {
                    
                    Trinity.exit(((TYInt) obj).getInternalInteger());
                    
                } else {
                    
                    TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Kernel.exit requires an integer.", stackTrace);
                    error.throwError();
                }
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "Kernel.exit receives one argument.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
    }
}
