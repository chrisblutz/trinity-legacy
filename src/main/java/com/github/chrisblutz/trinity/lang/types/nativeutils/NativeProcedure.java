package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.procedures.TYProcedureObject;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeProcedure {
    
    protected static void register() {
        
        TrinityNatives.registerForNativeConstruction(TrinityNatives.Classes.PROCEDURE);
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "initialize", (runtime, thisObj, params) -> {
            
            if (runtime.hasVariable("block")) {
                
                return runtime.getVariable("block");
                
            } else {
                
                return new TYProcedureObject(new TYProcedure((runtime11, thisObj1, params1) -> TYObject.NIL, false), new TYRuntime());
            }
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "getRequiredArguments", (runtime, thisObj, params) -> NativeStorage.getMandatoryArguments(TrinityNatives.cast(TYProcedureObject.class, thisObj).getInternalProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "getOptionalArguments", (runtime, thisObj, params) -> NativeStorage.getOptionalArguments(TrinityNatives.cast(TYProcedureObject.class, thisObj).getInternalProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "getBlockArgument", (runtime, thisObj, params) -> NativeStorage.getBlockArgument(TrinityNatives.cast(TYProcedureObject.class, thisObj).getInternalProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "getOverflowArgument", (runtime, thisObj, params) -> NativeStorage.getOverflowArgument(TrinityNatives.cast(TYProcedureObject.class, thisObj).getInternalProcedure()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "call", (runtime, thisObj, params) -> {
            
            TYProcedureObject obj = (TYProcedureObject) thisObj;
            TYProcedure proc = obj.getInternalProcedure();
            TYRuntime newRuntime = obj.getProcedureRuntime().clone();
            
            TYObject args = runtime.getVariable("args");
            List<TYObject> procParams = new ArrayList<>();
            if (args instanceof TYArray) {
                
                procParams.addAll(((TYArray) args).getInternalList());
            }
            
            TYProcedure subProc = null;
            TYRuntime subRuntime = null;
            if (proc.getBlockParameter() != null) {
                
                TYProcedureObject subBlock = TrinityNatives.cast(TYProcedureObject.class, runtime.getVariable("block"));
                subProc = subBlock.getInternalProcedure();
                subRuntime = subBlock.getProcedureRuntime();
            }
            
            TYObject result = proc.call(newRuntime, subProc, subRuntime, TYObject.NONE, procParams.toArray(new TYObject[procParams.size()]));
            
            newRuntime.disposeVariables(obj.getProcedureRuntime());
            
            obj.setBroken(newRuntime.isBroken());
            
            return result;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.PROCEDURE, "isBroken", (runtime, thisObj, params) -> {
            
            TYProcedureObject obj = (TYProcedureObject) thisObj;
            return TYBoolean.valueFor(obj.isBroken());
        });
    }
}
