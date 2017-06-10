package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeMath {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Math", "E", true, null, null, null, (runtime, thisObj, params) -> NativeStorage.getE());
        TrinityNatives.registerMethod("Trinity.Math", "PI", true, null, null, null, (runtime, thisObj, params) -> NativeStorage.getPi());
        TrinityNatives.registerMethod("Trinity.Math", "pow", true, new String[]{"x", "n"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            double n = TrinityNatives.toFloat(runtime.getVariable("n"));
            
            return TrinityNatives.wrapNumber(Math.pow(x, n));
        });
        TrinityNatives.registerMethod("Trinity.Math", "abs", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.abs(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "sqrt", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.sqrt(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "cbrt", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.cbrt(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "sin", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.sin(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "cos", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.cos(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "tan", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.tan(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "arcsin", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.asin(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "arccos", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.acos(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "arctan", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.atan(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "toDegrees", true, new String[]{"rad"}, null, null, (runtime, thisObj, params) -> {
            
            double rad = TrinityNatives.toFloat(runtime.getVariable("rad"));
            
            return TrinityNatives.wrapNumber(Math.toDegrees(rad));
        });
        TrinityNatives.registerMethod("Trinity.Math", "toRadians", true, new String[]{"deg"}, null, null, (runtime, thisObj, params) -> {
            
            double deg = TrinityNatives.toFloat(runtime.getVariable("deg"));
            
            return TrinityNatives.wrapNumber(Math.toRadians(deg));
        });
        Map<String, ProcedureAction> optionalParams = new HashMap<>();
        optionalParams.put("base", (runtime, thisObj, params) -> new TYInt(10));
        TrinityNatives.registerMethod("Trinity.Math", "log", true, new String[]{"x"}, optionalParams, null, (runtime, thisObj, params) -> {
            
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
        TrinityNatives.registerMethod("Trinity.Math", "ln", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.log(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "round", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.round(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "ceil", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.ceil(x));
        });
        TrinityNatives.registerMethod("Trinity.Math", "floor", true, new String[]{"x"}, null, null, (runtime, thisObj, params) -> {
            
            double x = TrinityNatives.toFloat(runtime.getVariable("x"));
            
            return TrinityNatives.wrapNumber(Math.floor(x));
        });
    }
}
