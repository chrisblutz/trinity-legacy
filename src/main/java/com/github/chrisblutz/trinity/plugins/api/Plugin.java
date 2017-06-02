package com.github.chrisblutz.trinity.plugins.api;

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
    
    public abstract void onEvent(int event, Object... args);
    
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
}
