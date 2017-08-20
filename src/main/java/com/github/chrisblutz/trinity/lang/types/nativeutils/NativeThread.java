package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
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
        
        TrinityNatives.registerForNativeConstruction("Trinity.Thread");
        
        TrinityNatives.registerMethod("Trinity.Thread", "initialize", (runtime, thisObj, params) -> {
            
            TYProcedure procedure = TrinityNatives.cast(TYProcedureObject.class, runtime.getVariable("block")).getInternalProcedure();
            String name = TrinityNatives.toString(runtime.getVariable("name"), runtime);
            
            return new TYThreadObject(new TYThread(name, procedure, runtime));
        });
        TrinityNatives.registerMethod("Trinity.Thread", "start", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            thread.start();
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Thread", "interrupt", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            thread.interrupt();
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Thread", "isAlive", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            return TYBoolean.valueFor(thread.isAlive());
        });
        TrinityNatives.registerMethod("Trinity.Thread", "isInterrupted", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            return TYBoolean.valueFor(thread.isInterrupted());
        });
        TrinityNatives.registerMethod("Trinity.Thread", "getName", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            return TrinityNatives.getObjectFor(thread.getName());
        });
        TrinityNatives.registerMethod("Trinity.Thread", "setErrorHandler", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            thread.setErrorHandler(TrinityNatives.cast(TYProcedureObject.class, runtime.getVariable("block")));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Thread", "getErrorHandler", (runtime, thisObj, params) -> {
            
            TYThread thread = TrinityNatives.cast(TYThreadObject.class, thisObj).getInternalThread();
            if (thread.getErrorHandler() != null) {
                
                return thread.getErrorHandler();
                
            } else {
                
                return TYObject.NIL;
            }
        });
        
        TrinityNatives.registerMethod("Trinity.Thread", "current", (runtime, thisObj, params) -> NativeStorage.getThreadObject(TYThread.getCurrentThread()));
        TrinityNatives.registerMethod("Trinity.Thread", "sleep", (runtime, thisObj, params) -> {
            
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
