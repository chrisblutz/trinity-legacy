package com.github.chrisblutz.trinity.plugins.api;

/**
 * @author Christopher Lutz
 */
public class Events {
    
    /**
     * This event triggers after loaded classes are finalized
     */
    public static final int CLASS_FINALIZATION = 0;
    
    // Loading events
    public static final int CLASS_LOAD = 10;
    
    // File loading events
    public static final int FILE_LOAD = 15;
    public static final int FILE_CLASS_LOAD = 16;
    
    // Variable update events
    public static final int GLOBAL_VARIABLE_UPDATE = 20;
    public static final int CLASS_VARIABLE_UPDATE = 21;
    
    // Method update events
    public static final int METHOD_UPDATE = 30;
    
    // Error events
    public static final int ERROR_THROWN = 100;
    public static final int ERROR_CAUGHT = 101;
}
