package com.github.chrisblutz.trinity.cli;

import com.github.chrisblutz.trinity.info.TrinityInfo;
import com.github.chrisblutz.trinity.libraries.Libraries;
import com.github.chrisblutz.trinity.runner.Runner;

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
    
    private static List<String> libraries = new ArrayList<>();
    
    private static String currentOption = null;
    private static List<String> params = new ArrayList<>();
    
    public static void parse(String[] args) {
        
        for (String arg : args) {
            
            parseArg(arg);
        }
        
        if (currentOption == null) {
            
            parseArguments(params);
            
        } else {
            
            parseOption(currentOption, params);
            
        }
    }
    
    public static void start() {
        
        handleLibraries(libraries);
        
        Runner.run(sourceFiles.toArray(new File[sourceFiles.size()]), mainClass, arguments.toArray(new String[arguments.size()]));
    }
    
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
                
                libraries.addAll(params);
                break;
            
            default:
                System.err.println("Unrecognized option '" + arg + "'.");
                break;
        }
    }
    
    private static void handleLibraries(List<String> params) {
        
        for (String lib : params) {
            
            Libraries.load(lib);
        }
    }
    
    public static boolean areAnyFilesLoaded() {
        
        return loadedAnyFiles;
    }
    
    public static boolean isDebuggingEnabled() {
        
        return debugging;
    }
}
