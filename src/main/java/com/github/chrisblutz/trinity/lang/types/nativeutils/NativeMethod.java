package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.TYMethodObject;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.Arrays;


/**
 * @author Christopher Lutz
 */
class NativeMethod {
    
    static void register() {
        
        TrinityNatives.registerMethod("Method", "getRequiredArguments", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMandatoryArguments(TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "getOptionalArguments", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getOptionalArguments(TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "getBlockArgument", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getBlockArgument(TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "invoke", false, new String[]{"obj"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject invokeThis = runtime.getVariable("obj");
            TYObject[] newParams = Arrays.copyOfRange(params, 1, params.length);
            TYMethod method = TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod();
            String name = method.getName();
            
            return method.getContainerClass().tyInvoke(name, runtime, stackTrace, null, null, invokeThis, newParams);
        });
        TrinityNatives.registerMethod("Method", "getName", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMethodName(TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "isStatic", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.isMethodStatic(TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod()));
        TrinityNatives.registerMethod("Method", "isNative", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.isMethodNative(TrinityNatives.cast(TYMethodObject.class, thisObj, stackTrace).getInternalMethod()));
    }
}
