package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.concurrent.ThreadLocalRandom;


/**
 * @author Christopher Lutz
 */
class NativeRandom {
    
    protected static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.RANDOM, "nextInt", (runtime, thisObj, params) -> {
            
            int lowerBound = TrinityNatives.toInt(runtime.getVariable("origin"));
            int upperBound = TrinityNatives.toInt(runtime.getVariable("bound"));
            
            return new TYInt(ThreadLocalRandom.current().nextInt(lowerBound, upperBound));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.RANDOM, "nextLong", (runtime, thisObj, params) -> {
            
            long lowerBound = TrinityNatives.toLong(runtime.getVariable("origin"));
            long upperBound = TrinityNatives.toLong(runtime.getVariable("bound"));
            
            return new TYLong(ThreadLocalRandom.current().nextLong(lowerBound, upperBound));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.RANDOM, "nextFloat", (runtime, thisObj, params) -> {
            
            double lowerBound = TrinityNatives.toFloat(runtime.getVariable("origin"));
            double upperBound = TrinityNatives.toFloat(runtime.getVariable("bound"));
            
            return new TYFloat(ThreadLocalRandom.current().nextDouble(lowerBound, upperBound));
        });
    }
}
