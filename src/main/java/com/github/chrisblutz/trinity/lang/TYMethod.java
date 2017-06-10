package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;


/**
 * @author Christopher Lutz
 */
public class TYMethod {
    
    private String name;
    private boolean staticMethod, nativeMethod, secureMethod;
    private TYClass containerClass;
    private TYProcedure procedure;
    private String[] importedModules = new String[0];
    
    private String[] leadingComments = null;
    
    public TYMethod(String name, boolean staticMethod, boolean nativeMethod, TYClass containerClass, TYProcedure procedure) {
        
        this(name, staticMethod, nativeMethod, false, containerClass, procedure);
    }
    
    public TYMethod(String name, boolean staticMethod, boolean nativeMethod, boolean secureMethod, TYClass containerClass, TYProcedure procedure) {
        
        this.name = name;
        this.staticMethod = staticMethod;
        this.nativeMethod = nativeMethod;
        this.secureMethod = secureMethod;
        this.containerClass = containerClass;
        this.procedure = procedure;
    }
    
    public String getName() {
        
        return name;
    }
    
    public boolean isStaticMethod() {
        
        return staticMethod;
    }
    
    public boolean isNativeMethod() {
        
        return nativeMethod;
    }
    
    public boolean isSecureMethod() {
        
        return secureMethod;
    }
    
    public void setSecureMethod(boolean secureMethod) {
        
        this.secureMethod = secureMethod;
    }
    
    public TYClass getContainerClass() {
        
        return containerClass;
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
    
    public String[] getLeadingComments() {
        
        return leadingComments;
    }
    
    public void setLeadingComments(String[] leadingComments) {
        
        this.leadingComments = leadingComments;
    }
}
