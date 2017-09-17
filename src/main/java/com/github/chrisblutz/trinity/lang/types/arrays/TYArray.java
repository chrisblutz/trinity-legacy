package com.github.chrisblutz.trinity.lang.types.arrays;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;

import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYArray extends TYObject {
    
    private List<TYObject> internalList;
    
    public TYArray(List<TYObject> internal) {
        
        super(ClassRegistry.getClass("Trinity.Array"));
        
        this.internalList = internal;
        
        // Make sure Array's instance fields are initialized, since its constructor is not called
        getObjectClass().initializeInstanceFields(this, new TYRuntime());
    }
    
    public List<TYObject> getInternalList() {
        
        return internalList;
    }
    
    public int size() {
        
        return getInternalList().size();
    }
}
