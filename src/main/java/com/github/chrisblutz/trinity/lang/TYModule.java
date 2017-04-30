package com.github.chrisblutz.trinity.lang;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYModule {
    
    private List<TYClass> classes = new ArrayList<>();
    private List<TYModule> modules = new ArrayList<>();
    private String name, shortName;
    private TYModule parentModule;
    
    public TYModule(String name, String shortName) {
        
        this.name = name;
        this.shortName = shortName;
    }
    
    public String getName() {
        
        return name;
    }
    
    public String getShortName() {
        
        return shortName;
    }
    
    public void addModule(TYModule tyModule) {
        
        modules.add(tyModule);
    }
    
    public List<TYModule> getModules() {
        
        return modules;
    }
    
    public boolean hasModule(String shortName) {
        
        for (TYModule tyModule : getModules()) {
            
            if (tyModule.getShortName().contentEquals(shortName)) {
                
                return true;
            }
        }
        
        return false;
    }
    
    public TYModule getModule(String shortName) {
        
        for (TYModule tyModule : getModules()) {
            
            if (tyModule.getShortName().contentEquals(shortName)) {
                
                return tyModule;
            }
        }
        
        return null;
    }
    
    public TYModule getParentModule() {
        
        return parentModule;
    }
    
    public void setParentModule(TYModule parentModule) {
    
        this.parentModule = parentModule;
    }
    
    public void addClass(TYClass tyClass) {
        
        classes.add(tyClass);
    }
    
    public List<TYClass> getClasses() {
        
        return classes;
    }
    
    public boolean hasClass(String shortName) {
        
        for (TYClass tyClass : getClasses()) {
            
            if (tyClass.getShortName().contentEquals(shortName)) {
                
                return true;
            }
        }
        
        return false;
    }
    
    public TYClass getClass(String shortName) {
        
        for (TYClass tyClass : getClasses()) {
            
            if (tyClass.getShortName().contentEquals(shortName)) {
                
                return tyClass;
            }
        }
        
        return null;
    }
}
