package com.github.chrisblutz.trinity.utils;

import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class ArrayUtils {
    
    public static boolean isSolid(TYArray array, TYRuntime runtime) {
        
        return TrinityNatives.toBoolean(array.tyInvoke("isSolid", runtime, null, null));
    }
}
