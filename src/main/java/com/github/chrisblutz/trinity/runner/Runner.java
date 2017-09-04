package com.github.chrisblutz.trinity.runner;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.plugins.api.Events;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Runner {
    
    private static Map<Thread, String> currentFiles = new HashMap<>();
    private static Map<Thread, Integer> currentLines = new HashMap<>();
    
    private static final ThreadGroup trinityThreadGroup = new ThreadGroup("TrinityThreads");
    
    public static String getCurrentFile(Thread thread) {
        
        return currentFiles.get(thread);
    }
    
    public static void setCurrentFile(String currentFile) {
        
        currentFiles.put(Thread.currentThread(), currentFile);
    }
    
    public static int getCurrentLine(Thread thread) {
        
        return currentLines.get(thread);
    }
    
    public static void setCurrentLine(int currentLine) {
        
        currentLines.put(Thread.currentThread(), currentLine);
    }
    
    public static void updateLocation(String currentFile, int currentLine) {
        
        setCurrentFile(currentFile);
        setCurrentLine(currentLine);
    }
    
    public static void run(File[] sourceFiles, String mainClass, String[] args) {
        
        parseAndRun(sourceFiles, mainClass, args);
    }
    
    private static void parseAndRun(File[] sourceFiles, String mainClass, String[] args) {
        
        // Parse source
        
        long startLoadMillis = System.currentTimeMillis();
        
        for (File file : sourceFiles) {
            
            TrinityParser.parse(file);
        }
        
        ClassRegistry.finalizeClasses();
        
        long endLoadMillis = System.currentTimeMillis();
        
        PluginLoader.triggerEvent(Events.CLASS_FINALIZATION);
        
        if (sourceFiles.length > 0) {
            
            // Run
            
            if (ClassRegistry.getMainClasses().size() == 0) {
                
                Errors.throwSyntaxError("Trinity.Errors.MethodNotFoundError", "No main method found in found in loaded files.", null, 0);
            }
            
            long startMillis = System.currentTimeMillis();
            
            ProcedureAction mainAction = null;
            if (mainClass != null) {
                
                if (ClassRegistry.classExists(mainClass)) {
                    
                    final TYClass main = ClassRegistry.getClass(mainClass);
                    
                    mainAction = (runtime, thisObj, params) -> {
                        
                        TrinityInterpreter.runInitializationActions();
                        return main.tyInvoke("main", runtime, null, null, TYObject.NONE, TrinityNatives.getArrayFor(args));
                    };
                    
                } else {
                    
                    Errors.throwSyntaxError("Trinity.Errors.ClassNotFoundError", "Class '" + mainClass + "' not found.", null, 0);
                }
                
            } else {
                
                if (ClassRegistry.getMainClasses().size() > 0) {
                    
                    final TYClass main = ClassRegistry.getMainClasses().get(0);
                    
                    mainAction = (runtime, thisObj, params) -> {
                        
                        TrinityInterpreter.runInitializationActions();
                        return main.tyInvoke("main", runtime, null, null, TYObject.NONE, TrinityNatives.getArrayFor(args));
                    };
                    
                } else {
                    
                    Errors.throwSyntaxError("Trinity.Errors.MethodNotFoundError", "No 'main' methods found.", null, 0);
                }
            }
            
            if (mainAction != null) {
                
                TYProcedure mainProcedure = new TYProcedure(mainAction, true);
                TYThread mainThread = TYThread.constructMainThread(mainProcedure);
                mainThread.start();
                
                try {
                    
                    waitForGroupCompletion();
                    
                } catch (InterruptedException e) {
                    
                    // Interrupted
                }
            }
            
            long endMillis = System.currentTimeMillis();
            
            if (CLI.isDebuggingEnabled()) {
                
                long loadTotal = endLoadMillis - startLoadMillis;
                long total = endMillis - startMillis;
                System.out.println(String.format("\nExecution took %.3f seconds (files took %.3f seconds to load).", (float) total / 1000f, (float) loadTotal / 1000f));
            }
        }
        
        // Trigger plugin unload, assumes execution succeeded
        PluginLoader.unloadAll(0);
    }
    
    private static void waitForGroupCompletion() throws InterruptedException {
        
        synchronized (trinityThreadGroup) {
            
            while (trinityThreadGroup.activeCount() > 0) {
                
                trinityThreadGroup.wait();
            }
        }
    }
    
    public static ThreadGroup getTrinityThreadGroup() {
        
        return trinityThreadGroup;
    }
}
