package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Christopher Lutz
 */
class NativeMap {
    
    protected static void register() {
        
        TrinityNatives.registerForNativeConstruction(TrinityNatives.Classes.MAP);
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "initialize", (runtime, thisObj, params) -> {
            
            int storageType = TrinityNatives.toInt(runtime.getVariable("storageType"));
            return new TYMap(getMapForStorageType(storageType), storageType);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "length", (runtime, thisObj, params) -> NativeStorage.getMapLength(TrinityNatives.cast(TYMap.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "keys", (runtime, thisObj, params) -> NativeStorage.getMapKeySet(TrinityNatives.cast(TYMap.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "values", (runtime, thisObj, params) -> NativeStorage.getMapValues(TrinityNatives.cast(TYMap.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "put", (runtime, thisObj, params) -> {
            
            put(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime.getVariable("value"), runtime);
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj));
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "remove", (runtime, thisObj, params) -> {
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj));
            
            return remove(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "clear", (runtime, thisObj, params) -> {
            
            TrinityNatives.cast(TYMap.class, thisObj).getInternalMap().clear();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "[]", (runtime, thisObj, params) -> get(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime.getVariable("defaultValue"), runtime));
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "[]=", (runtime, thisObj, params) -> {
            
            TYObject obj = put(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime.getVariable("value"), runtime);
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj));
            
            return obj;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.MAP, "getStorageType", (runtime, thisObj, params) -> new TYInt(TrinityNatives.cast(TYMap.class, thisObj).getStorageType()));
    }
    
    private static Map<TYObject, TYObject> getMapForStorageType(int storageType) {
        
        final int FAST_STORAGE = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getVariable("FAST_STORAGE").getValue());
        final int ORDERED_STORAGE = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getVariable("ORDERED_STORAGE").getValue());
        final int COMPARISON_STORAGE = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getVariable("COMPARISON_STORAGE").getValue());
        
        if (storageType == FAST_STORAGE) {
            
            return new HashMap<>();
            
        } else if (storageType == ORDERED_STORAGE) {
            
            return new LinkedHashMap<>();
            
        } else if (storageType == COMPARISON_STORAGE) {
            
            return new TreeMap<>(NativeHelper.getTYObjectComparator());
            
        } else {
            
            Errors.throwError(Errors.Classes.INVALID_ARGUMENT_ERROR, "Storage type " + storageType + " not valid.");
            return new HashMap<>();
        }
    }
    
    private static TYObject get(TYMap tyMap, TYObject obj, TYObject def, TYRuntime runtime) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            boolean equal = TrinityNatives.toBoolean(key.tyInvoke("==", runtime, null, null, obj));
            
            if (equal) {
                
                return map.get(key);
            }
        }
        
        return def;
    }
    
    private static TYObject put(TYMap tyMap, TYObject obj, TYObject value, TYRuntime runtime) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        boolean exists = false;
        for (TYObject key : map.keySet()) {
            
            boolean equal = TrinityNatives.toBoolean(key.tyInvoke("==", runtime, null, null, obj));
            
            if (equal) {
                
                exists = true;
                map.put(key, value);
            }
        }
        
        if (!exists) {
            
            return map.put(obj, value);
        }
        
        return TYObject.NIL;
    }
    
    private static TYObject remove(TYMap tyMap, TYObject obj, TYRuntime runtime) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            boolean equal = TrinityNatives.toBoolean(key.tyInvoke("==", runtime, null, null, obj));
            
            if (equal) {
                
                return map.remove(key);
            }
        }
        
        return TYObject.NIL;
    }
}
