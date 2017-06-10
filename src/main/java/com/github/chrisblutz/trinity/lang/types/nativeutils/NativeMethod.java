package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYMethodObject;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.Arrays;


/**
 * @author Christopher Lutz
 */
class NativeMethod {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Method", "getRequiredArguments", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.getMandatoryArguments(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "getOptionalArguments", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.getOptionalArguments(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "getBlockArgument", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.getBlockArgument(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "invoke", false, new String[]{"obj"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject invokeThis = runtime.getVariable("obj");
            TYObject[] newParams = Arrays.copyOfRange(params, 1, params.length);
            TYMethod method = TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod();
            String name = method.getName();
            
            return method.getContainerClass().tyInvoke(name, runtime, null, null, invokeThis, newParams);
        });
        TrinityNatives.registerMethod("Trinity.Method", "getName", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.getMethodName(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "isStatic", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.isMethodStatic(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "isNative", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.isMethodNative(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "isSecure", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.isMethodSecure(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod("Trinity.Method", "getComments", false, null, null, null, new ProcedureAction() {
            
            @Override
            public TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params) {
                
                TYMethod method = TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod();
                
                return NativeStorage.getLeadingComments(method);
            }
        });
    }
}
