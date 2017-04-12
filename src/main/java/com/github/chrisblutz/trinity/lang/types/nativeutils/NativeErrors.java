package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTraceElement;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.ArrayList;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class NativeErrors {
    
    public static void register(Map<String, TYMethod> methods) {
        
        methods.put("Trinity.Errors.Error.populateStackTrace", new TYMethod("populateStackTrace", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            TYArray ary = new TYArray(new ArrayList<>());
            
            for (int i = 1; i < stackTrace.getStackTrace().length; i++) {
                
                TYStackTraceElement e = stackTrace.getStackTrace()[i];
                
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
                TYObject stackTraceInstance = ClassRegistry.getClass("Trinity.Errors.StackTraceElement").tyInvoke("new", runtime, stackTrace, TYObject.NONE, errorClass, method, fileName, line);
                ary.getInternalList().add(stackTraceInstance);
            }
            
            Variables.getInstanceVariables(runtime.getScope()).put("stackTrace", ary);
            
            return TYObject.NONE;
        })));
    }
}
