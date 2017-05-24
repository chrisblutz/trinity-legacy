package com.github.chrisblutz.trinity.cli;

import com.github.chrisblutz.trinity.bootstrap.Bootstrap;
import com.github.chrisblutz.trinity.info.TrinityInfo;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYSyntaxError;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.parser.TrinityParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class CLI {
    
    private static boolean loadedAnyFiles = false;
    private static boolean debugging = false;
    
    private static List<File> sourceFiles = new ArrayList<>();
    private static String mainClass = null;
    
    private static List<String> arguments = new ArrayList<>();
    
    public static void start(String[] args) {
        
        for (String arg : args) {
            
            parseArg(arg);
        }
        
        if (currentOption != null) {
            
            parseOption(currentOption, params);
            
        } else {
            
            parseArguments(params);
        }
        
        run();
    }
    
    private static void run() {
        
        for (File file : sourceFiles) {
            
            TrinityParser.parse(file);
        }
        
        ClassRegistry.finalizeClasses();
        
        if (areAnyFilesLoaded()) {
            
            if (ClassRegistry.getMainClasses().size() == 0) {
                
                TYSyntaxError error = new TYSyntaxError("Trinity.Errors.MethodNotFoundError", "No main method found in found in loaded files.", null, 0);
                error.throwError();
            }
            
            long startMillis = System.currentTimeMillis();
            
            TrinityInterpreter.runPreMainInitializationCode();
            
            if (mainClass != null) {
                
                if (ClassRegistry.classExists(mainClass)) {
                    
                    TYClass main = ClassRegistry.getClass(mainClass);
                    
                    main.tyInvoke("main", new TYRuntime(), null, null, TYObject.NONE, parseIntoStringArray());
                    
                } else {
                    
                    TYSyntaxError error = new TYSyntaxError("Trinity.Errors.ClassNotFoundError", "Class '" + mainClass + "' not found.", null, 0);
                    error.throwError();
                }
                
            } else {
                
                if (ClassRegistry.getMainClasses().size() > 0) {
                    
                    ClassRegistry.getMainClasses().get(0).tyInvoke("main", new TYRuntime(), null, null, TYObject.NONE, parseIntoStringArray());
                    
                } else {
                    
                    TYSyntaxError error = new TYSyntaxError("Trinity.Errors.MethodNotFoundError", "No 'main' methods found.", null, 0);
                    error.throwError();
                }
            }
            
            long endMillis = System.currentTimeMillis();
            
            if (isDebuggingEnabled()) {
                
                long total = endMillis - startMillis;
                System.out.println(String.format("\nExecution took %.3f seconds.", (float) total / 1000f));
            }
        }
    }
    
    private static TYArray parseIntoStringArray() {
        
        List<TYObject> strings = new ArrayList<>();
        
        for (String arg : arguments) {
            
            strings.add(new TYString(arg));
        }
        
        return new TYArray(strings);
    }
    
    private static String currentOption = null;
    private static List<String> params = new ArrayList<>();
    
    private static void parseArg(String arg) {
        
        if (arg.startsWith("-")) {
            
            if (currentOption != null) {
                
                parseOption(currentOption, params);
                
            } else {
                
                parseArguments(params);
            }
            
            params.clear();
            
            currentOption = arg;
            
        } else {
            
            params.add(arg);
        }
    }
    
    private static void parseArguments(List<String> args) {
        
        for (String arg : args) {
            
            File file = new File(arg);
            
            if (file.exists()) {
                
                loadedAnyFiles = true;
                
                sourceFiles.add(file);
            }
        }
    }
    
    private static void parseOption(String arg, List<String> params) {
        
        switch (arg) {
            
            case "-d":
            case "--debug":
                
                debugging = true;
                break;
            
            case "-v":
            case "--version":
                
                System.out.println(TrinityInfo.getVersionString());
                break;
            
            case "-a":
            case "--arguments":
                
                arguments.addAll(params);
                break;
            
            case "-l":
            case "--lib":
                
                handleLibraries(params);
                break;
        }
    }
    
    private static void handleLibraries(List<String> params) {
        
        for (String lib : params) {
            
            switch (lib) {
                
                case "Trinity.UI":
                    
                    Bootstrap.bootstrapUI();
                    break;
            }
        }
    }
    
    public static boolean areAnyFilesLoaded() {
        
        return loadedAnyFiles;
    }
    
    public static boolean isDebuggingEnabled() {
        
        return debugging;
    }
}
