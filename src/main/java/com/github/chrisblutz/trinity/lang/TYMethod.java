package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;


/**
 * @author Christopher Lutz
 */
public class TYMethod {
    
    private String name;
    private boolean staticMethod;
    private TYProcedure procedure;
    private String[] importedModules = new String[0];
    
    public TYMethod(String name, boolean staticMethod, TYProcedure procedure) {
        
        this.name = name;
        this.staticMethod = staticMethod;
        this.procedure = procedure;
    }
    
    public String getName() {
        
        return name;
    }
    
    public boolean isStaticMethod() {
        
        return staticMethod;
    }
    
    public TYProcedure getProcedure() {
        
        return procedure;
    }
    
    public void importModules(String[] modules) {
        
        importedModules = modules;
    }
    
    public String[] getImportedModules() {
        
        return importedModules;
    }
}
