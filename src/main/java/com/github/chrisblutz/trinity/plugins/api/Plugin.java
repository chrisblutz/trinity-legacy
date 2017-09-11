package com.github.chrisblutz.trinity.plugins.api;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.libraries.NativeLibrary;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public abstract class Plugin {
    
    private String name, version;
    
    public void initialize(String name, String version) {
        
        this.name = name;
        this.version = version;
    }
    
    public abstract void load();
    
    public void unload(int exitCode) {
        
        if (exitCode == 0) {
            
            unloadClean();
            
        } else {
            
            unloadFast();
        }
    }
    
    /**
     * This method is called when the Trinity interpreter exits normally.
     */
    public abstract void unloadClean();
    
    /**
     * This method is called when the Trinity interpreter exits with an error.
     * It is recommended at that only vital operations are performed here, such
     * as closing I/O resources.
     */
    public abstract void unloadFast();
    
    public String getName() {
        
        return name;
    }
    
    public String getVersion() {
        
        return version;
    }
    
    /**
     * Invoked when the Trinity interpreter finishes loading an external library.
     *
     * @param libName   The name of the loaded library
     * @param file      The location of the bundled file containing the library
     * @param libraries The {@code NativeLibrary} classes provided by the library
     */
    public void onLibraryLoad(String libName, File file, NativeLibrary... libraries) {
        
        // Do nothing
    }
    
    /**
     * Invoked when the Trinity interpreter loads a file
     *
     * @param fileName The name of the file loaded (without the complete path)
     * @param file     The {@code File} that was loaded
     */
    public void onFileLoad(String fileName, File file) {
        
        // Do nothing
    }
    
    /**
     * Invoked when the Trinity interpreter loads a file containing a {@code class} declaration
     *
     * @param fileName The name of the file loaded (without the complete path)
     * @param file     The {@code File} loaded
     * @param tyClass  The resulting {@code TYClass} instance representing the class
     */
    public void onClassLoadFromFile(String fileName, File file, TYClass tyClass) {
        
        // Do nothing
    }
    
    /**
     * Invoked when the Trinity interpreter loads a {@code class} declaration
     *
     * @param tyClass The resulting {@code TYClass} instance representing the class
     */
    public void onClassLoad(TYClass tyClass) {
        
        // Do nothing
    }
    
    /**
     * Invoked when the Trinity interpreter performs class finalization after all
     * source files and libraries have been loaded.
     */
    public void onClassFinalization() {
        
        // Do nothing
    }
    
    /**
     * Invoked when a global variable's value changes
     *
     * @param globalName The name of the global variable
     * @param value      The new value of the variable
     */
    public void onGlobalVariableUpdate(String globalName, TYObject value) {
        
        // Do nothing
    }
    
    /**
     * Invoked when a method is added or changed within a class
     *
     * @param tyClass  The class containing the method
     * @param tyMethod The method that was updated
     */
    public void onMethodUpdate(TYClass tyClass, TYMethod tyMethod) {
        
        // Do nothing
    }
    
    /**
     * Invoked when an error is thrown during runtime.
     *
     * @param error The {@code Trinity.Errors.Error} object representing the error
     */
    public void onErrorThrown(TYObject error) {
        
        // Do nothing
    }
    
    /**
     * Invoked when a {@code catch} block catches an error thrown earlier
     *
     * @param error    The {@code Trinity.Errors.Error} object representing the error
     * @param fileName The name of the file in which the error was caught (without the complete path)
     * @param file     The file in which the error was caught
     * @param line     The line number at which the error was caught in the specified file
     */
    public void onErrorCaught(TYObject error, String fileName, File file, int line) {
        
        // Do nothing
    }
}
