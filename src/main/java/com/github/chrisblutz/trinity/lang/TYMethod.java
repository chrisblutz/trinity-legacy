package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.privelages.TYPrivileges;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TYMethod {
    
    public static final TYMethod NATIVE_INIT = new TYMethod("<init>", true, null, null);
    
    private String name;
    private boolean staticMethod;
    private TYPrivileges privileges;
    private TYProcedure procedure;
    private List<String> mandatoryParameters = new ArrayList<>();
    private Map<String, TYObject> optionalParameters = new HashMap<>();
    private String[] importedModules = new String[0];
    
    public TYMethod(String name, boolean staticMethod, TYPrivileges privileges, TYProcedure procedure) {
        
        this.name = name;
        this.staticMethod = staticMethod;
        this.privileges = privileges;
        this.procedure = procedure;
    }
    
    public String getName() {
        
        return name;
    }
    
    public boolean isStaticMethod() {
        
        return staticMethod;
    }
    
    public TYPrivileges getPrivileges() {
        
        return privileges;
    }
    
    public TYProcedure getProcedure() {
        
        return procedure;
    }
    
    public List<String> getMandatoryParameters() {
        
        return mandatoryParameters;
    }
    
    public void setMandatoryParameters(List<String> mandatoryParameters) {
        
        this.mandatoryParameters = mandatoryParameters;
    }
    
    public Map<String, TYObject> getOptionalParameters() {
        
        return optionalParameters;
    }
    
    public void setOptionalParameters(Map<String, TYObject> optionalParameters) {
        
        this.optionalParameters = optionalParameters;
    }
    
    public void importModules(String[] modules) {
        
        importedModules = modules;
    }
    
    public String[] getImportedModules() {
        
        return importedModules;
    }
}
