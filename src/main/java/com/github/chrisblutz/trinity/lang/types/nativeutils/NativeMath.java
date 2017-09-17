package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeMath {
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.MATH, "E", (runtime, thisObj, params) -> NativeStorage.getE());
        TrinityNatives.registerField(TrinityNatives.Classes.MATH, "PI", (runtime, thisObj, params) -> NativeStorage.getPi());
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "pow", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            double n = TrinityNatives.toFloat(runtime.getVariable("n"));
            
            return TrinityNatives.wrapNumber(Math.pow(x, n));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "abs", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.abs(x));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "sqrt", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            if (x < 0) {
                
                return TrinityNatives.wrapComplexNumber(0, Math.sqrt(Math.abs(x)));
                
            } else {
                
                return TrinityNatives.wrapNumber(Math.sqrt(x));
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "cbrt", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.cbrt(x));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "sin", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.sin(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "cos", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.cos(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "tan", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.tan(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "arcsin", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.asin(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "arccos", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.acos(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "arctan", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.atan(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "toDegrees", (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.toDegrees(rad));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "toRadians", (runtime, thisObj, params) -> {
            
            double deg = TrinityNatives.toFloat(runtime.getVariable("deg"));
            
            return TrinityNatives.wrapNumber(Math.toRadians(deg));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "log", (runtime, thisObj, params) -> {
            
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
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "ln", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.log(x));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "round", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.round(x));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "ceil", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.ceil(x));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MATH, "floor", (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.floor(x));
        });
    }
}
