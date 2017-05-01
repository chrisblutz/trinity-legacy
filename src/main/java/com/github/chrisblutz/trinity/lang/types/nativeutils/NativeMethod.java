package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.TYMethodObject;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.Arrays;


/**
 * @author Christopher Lutz
 */
class NativeMethod {
    
    static void register() {
        
        TrinityNatives.registerMethod("Method", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYMethod method = ((TYMethodObject) thisObj).getInternalMethod();
            
            String str = "Method [" + method.getName() + "]";
            
            if (method.isNativeMethod()) {
                
                str += " (native)";
                
            } else if (method.isStaticMethod()) {
                
                str += " (static)";
            }
            
            return new TYString(str);
        });
        TrinityNatives.registerMethod("Method", "getRequiredArguments", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMandatoryArguments(((TYMethodObject) thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "getOptionalArguments", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getOptionalArguments(((TYMethodObject) thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "getBlockArgument", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getBlockArgument(((TYMethodObject) thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "invoke", false, new String[]{"obj"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject invokeThis = runtime.getVariable("obj");
            TYObject[] newParams = Arrays.copyOfRange(params, 1, params.length);
            
            return ((TYMethodObject) thisObj).getInternalMethod().getProcedure().call(runtime, stackTrace, null, null, invokeThis, newParams);
        });
    }
}
