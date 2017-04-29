package com.github.chrisblutz.trinity.lang.types.nativeutils;

/**
 * @author Christopher Lutz
 */
public class NativeHelper {
    
    public static void registerDefaults() {
        
        NativeObject.register();
        NativeString.register();
        NativeBoolean.register();
        NativeArray.register();
        NativeMap.register();
        
        NativeInt.register();
        NativeLong.register();
        NativeFloat.register();
        
        NativeClass.register();
        NativeModule.register();
        
        NativeKernel.register();
        NativeSystem.register();
        
        NativeErrors.register();
        NativeFileSystem.register();
        NativeProcedure.register();
    }
}
