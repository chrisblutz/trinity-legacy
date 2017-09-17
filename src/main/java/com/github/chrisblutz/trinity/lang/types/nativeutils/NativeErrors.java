package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.StackElement;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TrinityStack;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;


/**
 * @author Christopher Lutz
 */
class NativeErrors {
    
    static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.ERROR, "populateStackTrace", (runtime, thisObj, params) -> {
            
            TYArray ary = new TYArray(new ArrayList<>());
            
            TrinityStack stack = TYThread.getCurrentThread().getTrinityStack();
            for (int i = 2 + thisObj.getSuperStackLevel(); i < stack.getStack().length; i++) {
                
                StackElement e = stack.getStack()[i];
                
                TYObject errorClass;
                if (e.getErrorClass() != null) {
                    
                    errorClass = new TYString(e.getErrorClass());
                    
                } else {
                    
                    errorClass = TYObject.NIL;
                }
                
                TYObject method;
                if (e.getMethod() != null) {
                    
                    method = new TYString(e.getMethod());
                    
                } else {
                    
                    method = TYObject.NIL;
                }
                
                TYObject fileName;
                if (e.getFile() != null) {
                    
                    fileName = new TYString(e.getFile());
                    
                } else {
                    
                    fileName = TYObject.NIL;
                }
                
                TYObject line = new TYInt(e.getLine());
                TYObject stackTraceInstance = TrinityNatives.newInstance("Trinity.Errors.StackTraceElement", runtime, errorClass, method, fileName, line);
                ary.getInternalList().add(stackTraceInstance);
            }
            
            return ary;
        });
    }
}
