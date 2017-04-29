package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
class NativeKernel {
    
    private static Scanner readlnSc = null;
    
    static void register() {
        
        TrinityNatives.registerMethod("Kernel", "print", true, new String[]{"str"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject obj = runtime.getVariable("str");
            TYObject strObj = obj.tyInvoke("toString", runtime, stackTrace, null, null);
            
            if (strObj instanceof TYString) {
                
                System.out.print(((TYString) strObj).getInternalString());
                
            } else if (strObj == null) {
                
                System.out.print("nil");
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Kernel", "readln", true, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            if (readlnSc == null) {
                
                readlnSc = new Scanner(System.in);
                
            } else {
                
                readlnSc.reset();
            }
            
            return new TYString(readlnSc.nextLine());
        });
        TrinityNatives.registerMethod("Kernel", "currentTimeMillis", true, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYLong(System.currentTimeMillis()));
        TrinityNatives.registerMethod("Kernel", "throw", true, new String[]{"error"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject error = runtime.getVariable("error");
            
            if (error.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Errors.Error"))) {
                
                String errorMessage = ((TYString) error.tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString();
                System.err.println(errorMessage);
                
                Trinity.exit(1);
                
                // TODO allow for catching errors
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Kernel", "exit", true, new String[]{"code"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject obj = runtime.getVariable("code");
            if (obj instanceof TYInt) {
                
                Trinity.exit(((TYInt) obj).getInternalInteger());
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Kernel.exit requires an integer.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NIL;
        });
    }
}
