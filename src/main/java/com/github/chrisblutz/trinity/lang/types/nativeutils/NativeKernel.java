package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
class NativeKernel {
    
    private static Scanner readlnSc = null;
    
    static void register() {
        
        TrinityNatives.registerMethod("Kernel", "print", true, new String[]{"str"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject obj = runtime.getVariable("str");
            TYObject strObj = obj.tyInvoke("toString", runtime, null, null);
            
            if (strObj instanceof TYString) {
                
                System.out.print(((TYString) strObj).getInternalString());
                
            } else if (strObj == null) {
                
                System.out.print("nil");
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Kernel", "readln", true, null, null, null, (runtime, thisObj, params) -> {
            
            if (readlnSc == null) {
                
                readlnSc = new Scanner(System.in);
                
            } else {
                
                readlnSc.reset();
            }
            
            return new TYString(readlnSc.nextLine());
        });
        TrinityNatives.registerMethod("Kernel", "throw", true, new String[]{"error"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject error = runtime.getVariable("error");
            
            if (error.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Errors.Error"))) {
                
                String errorMessage = TrinityNatives.cast(TYString.class, error.tyInvoke("toString", runtime, null, null)).getInternalString();
                System.err.println(errorMessage);
                
                Trinity.exit(1);
                
                // TODO allow for catching errors
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Kernel", "exit", true, new String[]{"code"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject obj = runtime.getVariable("code");
            Trinity.exit(TrinityNatives.cast(TYInt.class, obj).getInternalInteger());
            return TYObject.NONE;
        });
    }
}
