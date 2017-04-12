package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.types.TYClassClass;
import com.github.chrisblutz.trinity.lang.types.TYModuleClass;
import com.github.chrisblutz.trinity.lang.types.TYNilClass;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArrayClass;
import com.github.chrisblutz.trinity.lang.types.bool.TYBooleanClass;
import com.github.chrisblutz.trinity.lang.types.kernel.TYKernelClass;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloatClass;
import com.github.chrisblutz.trinity.lang.types.numeric.TYIntClass;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLongClass;

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
        
        TYClass nativeClass = new TYIntClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYLongClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYFloatClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYClassClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYModuleClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYNilClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYKernelClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYBooleanClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        nativeClass = new TYArrayClass();
        classes.put(nativeClass.getName(), nativeClass);
        
        NativeObject.register(methods);
        NativeString.register(methods);
        NativeErrors.register(methods);
    }
}
