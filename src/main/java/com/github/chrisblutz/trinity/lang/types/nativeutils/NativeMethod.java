package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.TYMethodObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeMethod {
    
    static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "getRequiredArguments", (runtime, thisObj, params) -> NativeStorage.getMandatoryArguments(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod().getProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "getOptionalArguments", (runtime, thisObj, params) -> NativeStorage.getOptionalArguments(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod().getProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "getBlockArgument", (runtime, thisObj, params) -> NativeStorage.getBlockArgument(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod().getProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "getOverflowArgument", (runtime, thisObj, params) -> NativeStorage.getOverflowArgument(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod().getProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "invoke", (runtime, thisObj, params) -> {
            
            TYObject invokeThis = runtime.getVariable("obj");
            
            TYObject args = runtime.getVariable("args");
            List<TYObject> methodParams = new ArrayList<>();
            if (args instanceof TYArray) {
                
                methodParams.addAll(((TYArray) args).getInternalList());
            }
            
            TYMethod method = TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod();
            String name = method.getName();
            
            return method.getContainerClass().tyInvoke(name, runtime, null, null, invokeThis, methodParams.toArray(new TYObject[methodParams.size()]));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "getName", (runtime, thisObj, params) -> NativeStorage.getMethodName(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "isStatic", (runtime, thisObj, params) -> NativeStorage.isMethodStatic(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "isNative", (runtime, thisObj, params) -> NativeStorage.isMethodNative(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "isSecure", (runtime, thisObj, params) -> NativeStorage.isMethodSecure(TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.METHOD, "getComments", (runtime, thisObj, params) -> {
            
            TYMethod method = TrinityNatives.cast(TYMethodObject.class, thisObj).getInternalMethod();
            
            return NativeStorage.getLeadingComments(method);
        });
    }
}
