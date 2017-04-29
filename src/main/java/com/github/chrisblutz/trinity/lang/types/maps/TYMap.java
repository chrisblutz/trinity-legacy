package com.github.chrisblutz.trinity.lang.types.maps;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TYMap extends TYObject {
    
    private Map<TYObject, TYObject> internalMap = new HashMap<>();
    
    public TYMap(Map<TYObject, TYObject> internal) {
        
        super(ClassRegistry.getClass("Map"));
        
        this.internalMap = internal;
    }
    
    public Map<TYObject, TYObject> getInternalMap() {
        
        return internalMap;
    }
    
    public int size() {
        
        return getInternalMap().size();
    }
}
