package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class NativeHelper {
    
    private static Map<String, TYClass> classes = new HashMap<>();
    private static Map<String, TYMethod> methods = new HashMap<>();
    
    public static void registerNativeClass(String className) {
        
        ClassRegistry.register(className, classes.get(className));
    }
    
    public static TYMethod getNativeMethod(String methodName) {
        
        return methods.get(methodName);
    }
    
    static {
        
        NativeObject.register(methods);
        NativeString.register(methods);
        NativeBoolean.register(methods);
        NativeArray.register(methods);
        
        NativeInt.register(methods);
        NativeLong.register(methods);
        NativeFloat.register(methods);
        
        NativeClass.register(methods);
        NativeModule.register(methods);
        
        NativeKernel.register(methods);
        NativeSystem.register(methods);
        
        NativeErrors.register(methods);
        NativeFileSystem.register(methods);
        NativeProcedure.register(methods);
    }
    
    static void appendToStackTrace(TYStackTrace stackTrace, String errorClass, String method) {
        
        stackTrace.add(errorClass, method, null, 0);
    }
}
