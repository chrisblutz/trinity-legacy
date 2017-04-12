package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYModule;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class InterpretEnvironment {
    
    private List<TYModule> moduleStack;
    private List<TYClass> classStack;
    
    public InterpretEnvironment() {
        
        this(new ArrayList<>(), new ArrayList<>());
    }
    
    public InterpretEnvironment(List<TYModule> moduleStack, List<TYClass> classStack) {
        
        this.moduleStack = moduleStack;
        this.classStack = classStack;
    }
    
    public List<TYModule> getModuleStack() {
        
        return moduleStack;
    }
    
    public TYModule getLastModule() {
        
        return getModuleStack().get(getModuleStack().size() - 1);
    }
    
    public List<TYClass> getClassStack() {
        
        return classStack;
    }
    
    public TYClass getLastClass() {
        
        return getClassStack().get(getClassStack().size() - 1);
    }
    
    public boolean isEmpty() {
        
        return getModuleStack().isEmpty() && getClassStack().isEmpty();
    }
    
    public InterpretEnvironment append(TYClass tyClass) {
        
        List<TYClass> classes = new ArrayList<>();
        classes.addAll(getClassStack());
        classes.add(tyClass);
        
        if (getClassStack().isEmpty() && !getModuleStack().isEmpty()) {
            
            getLastModule().addClass(tyClass);
            
        } else if (!getClassStack().isEmpty()) {
            
            getLastClass().addClass(tyClass);
        }
        
        return new InterpretEnvironment(getModuleStack(), classes);
    }
    
    public InterpretEnvironment append(TYModule tyModule) {
        
        List<TYModule> modules = new ArrayList<>();
        modules.addAll(getModuleStack());
        modules.add(tyModule);
        
        if (!getModuleStack().isEmpty()) {
            
            getLastModule().addModule(tyModule);
        }
        
        return new InterpretEnvironment(modules, new ArrayList<>());
    }
    
    public String getEnvironmentString() {
        
        StringBuilder str = new StringBuilder();
        
        for (int i = 0; i < getModuleStack().size(); i++) {
            
            str.append(getModuleStack().get(i).getShortName());
            
            if (i < getModuleStack().size() - 1) {
                
                str.append(".");
            }
        }
        
        if (!getModuleStack().isEmpty() && !getClassStack().isEmpty()) {
            
            str.append(".");
        }
        
        for (int i = 0; i < getClassStack().size(); i++) {
            
            str.append(getClassStack().get(i).getShortName());
            
            if (i < getClassStack().size() - 1) {
                
                str.append(".");
            }
        }
        
        return str.toString();
    }
}
