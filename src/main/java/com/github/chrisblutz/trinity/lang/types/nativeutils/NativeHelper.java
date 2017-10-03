package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.Comparator;


/**
 * @author Christopher Lutz
 */
public class NativeHelper {
    
    private static Comparator<TYObject> comparator = null;
    
    public static void registerDefaults() {
        
        NativeObject.register();
        NativeString.register();
        NativeBoolean.register();
        NativeArray.register();
        NativeMap.register();
        
        NativeNumeric.register();
        NativeInt.register();
        NativeLong.register();
        NativeFloat.register();
        
        NativeClass.register();
        NativeModule.register();
        NativeMethod.register();
        NativeField.register();
        
        NativeKernel.register();
        NativeSystem.register();
        
        NativeThread.register();
        
        NativeErrors.register();
        NativeFileSystem.register();
        NativeProcedure.register();
        
        NativeMath.register();
        
        NativeOutputStream.register();
    }
    
    public static Comparator<TYObject> getTYObjectComparator() {
        
        if (comparator == null) {
            
            comparator = (o1, o2) -> TrinityNatives.toInt(o1.tyInvoke("compareTo", new TYRuntime(), null, null, o2));
        }
        
        return comparator;
    }
}
