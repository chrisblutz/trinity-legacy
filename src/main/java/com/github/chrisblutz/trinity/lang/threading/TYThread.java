package com.github.chrisblutz.trinity.lang.threading;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.interpreter.errors.TrinityErrorException;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TrinityStack;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.procedures.TYProcedureObject;
import com.github.chrisblutz.trinity.runner.Runner;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TYThread {
    
    private static Map<Thread, TYThread> threads = new HashMap<>();
    
    private String name;
    private Thread thread;
    
    private TrinityStack trinityStack;
    
    private TYProcedureObject errorHandler = null;
    
    public TYThread(String name, TYProcedure procedure, TYRuntime runtime) {
        
        this.name = name;
        thread = new Thread(Runner.getTrinityThreadGroup(), () -> {
            
            if (runtime != null) {
                
                TYRuntime newRuntime = runtime.clone();
                procedure.call(newRuntime, null, null, TYObject.NONE);
            }
        });
        thread.setUncaughtExceptionHandler((t, e) -> {
            
            TYThread tyThread = TYThread.getThread(t);
            
            if (errorHandler != null && e instanceof TrinityErrorException) {
                
                try {
                    
                    errorHandler.getInternalProcedure().call(errorHandler.getProcedureRuntime(), null, null, TYObject.NONE, ((TrinityErrorException) e).getErrorObject());
                    
                } catch (Exception e2) {
                    
                    Errors.throwUncaughtJavaException(e2, Runner.getCurrentFile(thread), Runner.getCurrentLine(thread), tyThread);
                    
                    Trinity.exit(1);
                }
                
            } else {
                
                Errors.throwUncaughtJavaException(e, Runner.getCurrentFile(thread), Runner.getCurrentLine(thread), tyThread);
                
                Trinity.exit(1);
            }
        });
        thread.setName(name);
        
        trinityStack = new TrinityStack(null);
        
        threads.put(thread, this);
    }
    
    public void start() {
        
        TrinityStack parent = null;
        TYThread current = getCurrentThread();
        if (current != null) {
            
            parent = current.getTrinityStack();
        }
        trinityStack = new TrinityStack(parent);
        
        thread.start();
    }
    
    public void interrupt() {
        
        thread.interrupt();
    }
    
    public boolean isAlive() {
        
        return thread.isAlive();
    }
    
    public boolean isInterrupted() {
        
        return thread.isInterrupted();
    }
    
    public String getName() {
        
        return name;
    }
    
    public void setErrorHandler(TYProcedureObject errorHandler) {
        
        this.errorHandler = errorHandler;
    }
    
    public TYProcedureObject getErrorHandler() {
        
        return errorHandler;
    }
    
    public TrinityStack getTrinityStack() {
        
        return trinityStack;
    }
    
    public static TYThread getThread(Thread thread) {
        
        return threads.get(thread);
    }
    
    public static TYThread getCurrentThread() {
        
        return getThread(Thread.currentThread());
    }
    
    public static TYThread constructMainThread(TYProcedure procedure) {
        
        return new TYThread("main", procedure, new TYRuntime());
    }
}
