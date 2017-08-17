package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeMap {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Map", "initialize", (runtime, thisObj, params) -> new TYMap(new HashMap<>()));
        TrinityNatives.registerMethod("Trinity.Map", "length", (runtime, thisObj, params) -> NativeStorage.getMapLength(TrinityNatives.cast(TYMap.class, thisObj)));
        TrinityNatives.registerMethod("Trinity.Map", "keys", (runtime, thisObj, params) -> NativeStorage.getMapKeySet(TrinityNatives.cast(TYMap.class, thisObj)));
        TrinityNatives.registerMethod("Trinity.Map", "values", (runtime, thisObj, params) -> NativeStorage.getMapValues(TrinityNatives.cast(TYMap.class, thisObj)));
        TrinityNatives.registerMethod("Trinity.Map", "put", (runtime, thisObj, params) -> {
            
            put(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime.getVariable("value"), runtime);
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj));
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Map", "remove", (runtime, thisObj, params) -> {
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj));
            
            return remove(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime);
        });
        TrinityNatives.registerMethod("Trinity.Map", "clear", (runtime, thisObj, params) -> {
            
            TrinityNatives.cast(TYMap.class, thisObj).getInternalMap().clear();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Map", "[]", (runtime, thisObj, params) -> get(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime));
        TrinityNatives.registerMethod("Trinity.Map", "[]=", (runtime, thisObj, params) -> {
            
            put(TrinityNatives.cast(TYMap.class, thisObj), runtime.getVariable("key"), runtime.getVariable("value"), runtime);
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj));
            
            return TYObject.NONE;
        });
    }
    
    private static TYObject get(TYMap tyMap, TYObject obj, TYRuntime runtime) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            boolean equal = TrinityNatives.toBoolean(key.tyInvoke("==", runtime, null, null, obj));
            
            if (equal) {
                
                return map.get(key);
            }
        }
        
        return TYObject.NIL;
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
            
            map.put(obj, value);
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
