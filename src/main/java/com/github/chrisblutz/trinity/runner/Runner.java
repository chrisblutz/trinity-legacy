package com.github.chrisblutz.trinity.runner;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.plugins.api.Events;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class Runner {
    
    private static String currentFile;
    private static int currentLine;
    
    private static Thread trinityThread;
    
    public static String getCurrentFile() {
        
        return currentFile;
    }
    
    public static void setCurrentFile(String currentFile) {
        
        Runner.currentFile = currentFile;
    }
    
    public static int getCurrentLine() {
        
        return currentLine;
    }
    
    public static void setCurrentLine(int currentLine) {
        
        Runner.currentLine = currentLine;
    }
    
    public static void updateLocation(String currentFile, int currentLine) {
        
        setCurrentFile(currentFile);
        setCurrentLine(currentLine);
    }
    
    public static void run(File[] sourceFiles, String mainClass, String[] args) {
        
        trinityThread = new Thread(() -> parseAndRun(sourceFiles, mainClass, args));
        trinityThread.setName("Trinity-Main");
        trinityThread.setUncaughtExceptionHandler((t, e) -> {
            
            Errors.throwUncaughtJavaException(e, getCurrentFile(), getCurrentLine());
            
            Trinity.exit(1);
        });
        trinityThread.start();
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
            
            TrinityInterpreter.runPreMainInitializationCode();
            
            if (mainClass != null) {
                
                if (ClassRegistry.classExists(mainClass)) {
                    
                    TYClass main = ClassRegistry.getClass(mainClass);
                    
                    main.tyInvoke("main", new TYRuntime(), null, null, TYObject.NONE, TrinityNatives.getArrayFor(args));
                    
                } else {
                    
                    Errors.throwSyntaxError("Trinity.Errors.ClassNotFoundError", "Class '" + mainClass + "' not found.", null, 0);
                }
                
            } else {
                
                if (ClassRegistry.getMainClasses().size() > 0) {
                    
                    ClassRegistry.getMainClasses().get(0).tyInvoke("main", new TYRuntime(), null, null, TYObject.NONE, TrinityNatives.getArrayFor(args));
                    
                } else {
                    
                    Errors.throwSyntaxError("Trinity.Errors.MethodNotFoundError", "No 'main' methods found.", null, 0);
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
}
