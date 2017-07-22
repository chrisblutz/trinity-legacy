package com.github.chrisblutz.trinity.lang.variables;

import com.github.chrisblutz.trinity.interpreter.Scope;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;


/**
 * @author Christopher Lutz
 */
public class VariableLoc {
    
    private TYClass containerClass = null;
    private Scope scope = null;
    private boolean constant = false;
    
    public TYObject getValue() {
        
        return VariableManager.getVariable(this);
    }
    
    public TYClass getContainerClass() {
        
        return containerClass;
    }
    
    public void setContainerClass(TYClass containerClass) {
        
        this.containerClass = containerClass;
    }
    
    public Scope getScope() {
        
        return scope;
    }
    
    public void setScope(Scope scope) {
        
        this.scope = scope;
    }
    
    public boolean isConstant() {
        
        return constant;
    }
    
    public void setConstant(boolean constant) {
        
        this.constant = constant;
    }
}
