package com.github.chrisblutz.trinity.lang.types.arrays;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;

import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYArray extends TYObject {
    
    private List<TYObject> internalList;
    
    public TYArray(List<TYObject> internal) {
        
        super(ClassRegistry.getClass("Array"));
        
        this.internalList = internal;
    }
    
    public List<TYObject> getInternalList() {
        
        return internalList;
    }
    
    public int size() {
        
        return getInternalList().size();
    }
}
