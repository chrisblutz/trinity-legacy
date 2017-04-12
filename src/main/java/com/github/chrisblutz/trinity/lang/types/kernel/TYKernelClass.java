package com.github.chrisblutz.trinity.lang.types.kernel;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidArgumentNumberError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidTypeError;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
public class TYKernelClass extends TYClass {
    
    public TYKernelClass() {
        
        super("Kernel", "Kernel", null, null);
        
        registerMethod(new TYMethod("print", true, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                TYObject strObj = obj.tyInvoke("toString", runtime, stackTrace);
                
                if (strObj instanceof TYString) {
                    
                    System.out.print(((TYString) strObj).getInternalString());
                    
                } else if (strObj == null) {
                    
                    System.out.print("nil");
                }
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "Kernel.print receives one argument.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
        registerMethod(new TYMethod("println", true, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 0 || params[0] == TYObject.NONE) {
                
                tyInvoke("print", runtime, stackTrace, thisObj, new TYString("\n"));
                
            } else if (params.length == 1) {
                
                tyInvoke("print", runtime, stackTrace, thisObj, params);
                tyInvoke("print", runtime, stackTrace, thisObj, new TYString("\n"));
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "Kernel.println receives zero or one argument(s).", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
        registerMethod(new TYMethod("readln", true, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            Scanner sc = new Scanner(System.in);
            return new TYString(sc.nextLine());
        })));
        registerMethod(new TYMethod("currentTimeMillis", true, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 0 || params[0] == TYObject.NONE) {
                
                return new TYLong(System.currentTimeMillis());
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "Kernel.currentTimeMillis takes no argument(s).", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
        registerMethod(new TYMethod("throw", true, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0 && params[0].getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Errors.Error"))) {
                
                String errorMessage = ((TYString) params[0].tyInvoke("toString", runtime, stackTrace)).getInternalString();
                System.err.println(errorMessage);
                
                Trinity.exit(1);
                
                // TODO allow for catching errors
            }
            
            return TYObject.NONE;
        })));
        registerMethod(new TYMethod("exit", true, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length == 1) {
                
                TYObject obj = params[0];
                if (obj instanceof TYInt) {
                    
                    Trinity.exit(((TYInt) obj).getInternalInteger());
                    
                } else {
                    
                    TYError error = new TYError(new TYInvalidTypeError(), "Kernel.exit requires an integer.", stackTrace);
                    error.throwError();
                }
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "Kernel.exit receives one argument.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NONE;
        })));
    }
}
