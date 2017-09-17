package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.procedures.TYProcedureObject;
import com.github.chrisblutz.trinity.lang.types.threading.TYThreadObject;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeThread {
    
    static void register() {
        
        TrinityNatives.registerForNativeConstruction(TrinityNatives.Classes.THREAD);
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "initialize", (runtime, thisObj, params) -> {
            
            TYProcedureObject procedureObj = TrinityNatives.cast(TYProcedureObject.class, runtime.getVariable("block"));
            String name = TrinityNatives.toString(runtime.getVariable("name"), runtime);
            
            return new TYThreadObject(new TYThread(name, procedureObj.getInternalProcedure(), procedureObj.getProcedureRuntime()));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "start", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            thread.start();
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "interrupt", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            thread.interrupt();
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "isAlive", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            return TYBoolean.valueFor(thread.isAlive());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "isInterrupted", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            return TYBoolean.valueFor(thread.isInterrupted());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "getName", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            return TrinityNatives.getObjectFor(thread.getName());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "setErrorHandler", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            thread.setErrorHandler(TrinityNatives.cast(TYProcedureObject.class, runtime.getVariable("block")));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "getErrorHandler", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            if (thread.getErrorHandler() != null) {
                
                return thread.getErrorHandler();
                
            } else {
                
                return TYObject.NIL;
            }
        });
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "current", (runtime, thisObj, params) -> NativeStorage.getThreadObject(TYThread.getCurrentThread()));
        TrinityNatives.registerMethod(TrinityNatives.Classes.THREAD, "sleep", (runtime, thisObj, params) -> {
            
            long millis = TrinityNatives.toLong(runtime.getVariable("millis"));
            try {
                
                Thread.sleep(millis);
                
            } catch (InterruptedException e) {
                
                // TODO
            }
            return TYObject.NONE;
        });
    }
}
