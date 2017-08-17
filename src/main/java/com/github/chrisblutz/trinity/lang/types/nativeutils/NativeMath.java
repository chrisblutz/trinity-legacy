package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeMath {
    
    static void register() {
        
        TrinityNatives.registerField("Trinity.Math", "E", (runtime, thisObj, params) -> NativeStorage.getE());
        TrinityNatives.registerField("Trinity.Math", "PI", (runtime, thisObj, params) -> NativeStorage.getPi());
        
        TrinityNatives.registerMethod("Trinity.Math", "pow", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            double n = TrinityNatives.toFloat(runtime.getVariable("n"));
            
            return TrinityNatives.wrapNumber(Math.pow(x, n));
        });
        TrinityNatives.registerMethod("Trinity.Math", "abs", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.abs(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "sqrt", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            if (x < 0) {
                
                return TrinityNatives.wrapComplexNumber(0, Math.sqrt(Math.abs(x)));
                
            } else {
                
                return TrinityNatives.wrapNumber(Math.sqrt(x));
            }
        });
        TrinityNatives.registerMethod("Trinity.Math", "cbrt", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.cbrt(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "sin", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.sin(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "cos", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.cos(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "tan", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.tan(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "arcsin", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.asin(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "arccos", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.acos(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "arctan", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.atan(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "toDegrees", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.toDegrees(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "toRadians", (runtime, thisObj, params) -> {
            
            double deg = TrinityNatives.toFloat(runtime.getVariable("deg"));
            
            return TrinityNatives.wrapNumber(Math.toRadians(deg));
        });
        TrinityNatives.registerMethod("Trinity.Math", "log", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            double base = TrinityNatives.toFloat(runtime.getVariable("base"));
            
            double result;
            
            if (base == 10) {
                
                result = Math.log(x);
                
            } else {
                
                result = Math.log(x) / Math.log(base);
            }
            
            return TrinityNatives.wrapNumber(result);
        });
        TrinityNatives.registerMethod("Trinity.Math", "ln", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.log(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "round", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.round(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "ceil", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.ceil(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "floor", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.floor(x));
        });
    }
}
