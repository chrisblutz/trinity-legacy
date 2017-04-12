package com.github.chrisblutz.trinity.cli;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYClassNotFoundError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYMethodNotFoundError;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.parser.TrinityParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class CLI {
    
    private static boolean debugging = false;
    
    private static List<File> sourceFiles = new ArrayList<>();
    private static String mainClass = null;
    
    private static List<String> arguments = new ArrayList<>();
    
    public static void start(String[] args) {
        
        for (String arg : args) {
            
            parseArg(arg);
        }
        
        run();
    }
    
    private static void run() {
        
        for (File file : sourceFiles) {
            
            TrinityParser.parse(file);
        }
        
        if (ClassRegistry.getMainClasses().size() == 0) {
        
        
        }
        
        if (mainClass != null) {
            
            if (ClassRegistry.classExists(mainClass)) {
                
                TYClass main = ClassRegistry.getClass(mainClass);
                
                main.tyInvoke("main", new TYRuntime(), new TYStackTrace(), TYObject.NONE, parseIntoStringArray());
                
            } else {
                
                TYError error = new TYError(new TYClassNotFoundError(), "Class '" + mainClass + "' not found.", new TYStackTrace());
                error.throwError();
            }
            
        } else {
            
            if (ClassRegistry.getMainClasses().size() > 0) {
                
                ClassRegistry.getMainClasses().get(0).tyInvoke("main", new TYRuntime(), new TYStackTrace(), TYObject.NONE, parseIntoStringArray());
                
            } else {
                
                TYError error = new TYError(new TYMethodNotFoundError(), "No 'main' methods found.", new TYStackTrace());
                error.throwError();
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
                
                parseOption(currentOption, params.toArray(new String[params.size()]));
                
            } else {
                
                parseArguments(params.toArray(new String[params.size()]));
            }
            
            params.clear();
            
            currentOption = arg;
            
        } else {
            
            params.add(arg);
        }
        
        if (currentOption != null) {
            
            parseOption(currentOption, params.toArray(new String[params.size()]));
            
        } else {
            
            parseArguments(params.toArray(new String[params.size()]));
        }
    }
    
    private static void parseArguments(String[] args) {
        
        for (String arg : args) {
            
            File file = new File(arg);
            
            if (file.exists()) {
                
                sourceFiles.add(file);
            }
        }
    }
    
    private static void parseOption(String arg, String... params) {
        
        switch (arg) {
            
            case "-d":
            case "--debug":
                
                debugging = true;
                break;
            
            case "-a":
            case "--arguments":
                
                arguments.addAll(Arrays.asList(params));
                break;
        }
    }
}
