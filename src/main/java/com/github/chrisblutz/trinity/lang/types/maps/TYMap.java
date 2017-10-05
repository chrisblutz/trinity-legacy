package com.github.chrisblutz.trinity.lang.types.maps;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.types.nativeutils.NativeHelper;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Christopher Lutz
 */
public class TYMap extends TYObject {
    
    private static int fastStorage = -1, orderedStorage = -1, comparisonStorage = -1;
    
    private Map<TYObject, TYObject> internalMap;
    private int storageType;
    
    public TYMap(Map<TYObject, TYObject> internal, int storageType) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.MAP));
        
        this.internalMap = internal;
        this.storageType = storageType;
    }
    
    public Map<TYObject, TYObject> getInternalMap() {
        
        return internalMap;
    }
    
    public int getStorageType() {
        
        return storageType;
    }
    
    public void setStorageType(int storageType) {
        
        Map<TYObject, TYObject> newMap;
        
        if (storageType == TYMap.getFastStorage()) {
            
            newMap = new HashMap<>();
            
        } else if (storageType == TYMap.getOrderedStorage()) {
            
            newMap = new LinkedHashMap<>();
            
        } else if (storageType == TYMap.getComparisonStorage()) {
            
            newMap = new TreeMap<>(NativeHelper.getTYObjectComparator());
            
        } else {
            
            Errors.throwError(Errors.Classes.INVALID_ARGUMENT_ERROR, "Storage type " + storageType + " not valid.");
            newMap = new HashMap<>();
        }
        
        newMap.putAll(internalMap);
        
        internalMap = newMap;
        
        NativeStorage.clearMapData(this);
    }
    
    public int size() {
        
        return getInternalMap().size();
    }
    
    public static int getFastStorage() {
        
        if (fastStorage == -1) {
            
            fastStorage = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getVariable("FAST_STORAGE").getValue());
        }
        
        return fastStorage;
    }
    
    public static int getOrderedStorage() {
        
        if (orderedStorage == -1) {
            
            orderedStorage = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getVariable("ORDERED_STORAGE").getValue());
        }
        
        return orderedStorage;
    }
    
    public static int getComparisonStorage() {
        
        if (comparisonStorage == -1) {
            
            comparisonStorage = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getVariable("COMPARISON_STORAGE").getValue());
        }
        
        return comparisonStorage;
    }
}
