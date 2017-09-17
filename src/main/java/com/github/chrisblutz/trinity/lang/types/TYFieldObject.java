package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYFieldObject extends TYObject {
    
    private TYClass internalClass;
    private String internalName;
    
    private boolean isNative, isStatic, isConstant;
    private String[] leadingComments;
    
    public TYFieldObject(TYClass tyClass, String name) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.FIELD));
        
        this.internalClass = tyClass;
        this.internalName = name;
        
        isNative = tyClass.isFieldNative(name);
        isStatic = tyClass.isFieldStatic(name);
        isConstant = tyClass.isFieldConstant(name);
        
        leadingComments = tyClass.getFieldLeadingComments(name);
    }
    
    public TYClass getInternalClass() {
        
        return internalClass;
    }
    
    public String getInternalName() {
        
        return internalName;
    }
    
    public boolean isNative() {
        
        return isNative;
    }
    
    public boolean isStatic() {
        
        return isStatic;
    }
    
    public boolean isConstant() {
        
        return isConstant;
    }
    
    public String[] getLeadingComments() {
        
        return leadingComments;
    }
}
