package com.github.chrisblutz.trinity.runner;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.TrinityParser;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class Runner {
    
    private static String currentFile;
    private static int currentLine;
    
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
        
        Thread trinityThread = new Thread(() -> parseAndRun(sourceFiles, mainClass, args));
        trinityThread.setName("Trinity-Main");
        trinityThread.setUncaughtExceptionHandler((t, e) -> {
            
            System.err.println("An error occurred in the Trinity interpreter in file '" + getCurrentFile() + "' at line " + getCurrentLine() + ".");
            
            if (CLI.isDebuggingEnabled()) {
                
                System.err.println("\n== FULL ERROR ==\n");
                e.printStackTrace();
                
            } else {
                
                System.err.println("To view a full trace, enable debugging with the -d/--debug option.");
            }
        });
        trinityThread.start();
    }
    
    private static void parseAndRun(File[] sourceFiles, String mainClass, String[] args) {
        
        // Parse source
        
        for (File file : sourceFiles) {
            
            TrinityParser.parse(file);
        }
        
        ClassRegistry.finalizeClasses();
        
        if (sourceFiles.length > 0) {
            
            // Run
            
            if (ClassRegistry.getMainClasses().size() == 0) {
                
                Errors.throwError("Trinity.Errors.MethodNotFoundError", "No main method found in found in loaded files.", null, 0);
            }
            
            long startMillis = System.currentTimeMillis();
            
            TrinityInterpreter.runPreMainInitializationCode();
            
            if (mainClass != null) {
                
                if (ClassRegistry.classExists(mainClass)) {
                    
                    TYClass main = ClassRegistry.getClass(mainClass);
                    
                    main.tyInvoke("main", new TYRuntime(), null, null, TYObject.NONE, TrinityNatives.getArrayFor(args));
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.ClassNotFoundError", "Class '" + mainClass + "' not found.", null, 0);
                }
                
            } else {
                
                if (ClassRegistry.getMainClasses().size() > 0) {
                    
                    ClassRegistry.getMainClasses().get(0).tyInvoke("main", new TYRuntime(), null, null, TYObject.NONE, TrinityNatives.getArrayFor(args));
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.MethodNotFoundError", "No 'main' methods found.", null, 0);
                }
            }
            
            long endMillis = System.currentTimeMillis();
            
            if (CLI.isDebuggingEnabled()) {
                
                long total = endMillis - startMillis;
                System.out.println(String.format("\nExecution took %.3f seconds.", (float) total / 1000f));
            }
        }
    }
}
