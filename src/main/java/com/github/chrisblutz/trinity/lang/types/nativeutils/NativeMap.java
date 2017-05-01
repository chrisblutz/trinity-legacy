package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeMap {
    
    static void register() {
        
        TrinityNatives.registerMethod("Map", "initialize", false, null, null, null, (runtime, stackTrace, thisObj, params) -> new TYMap(new HashMap<>()));
        TrinityNatives.registerMethod("Map", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            StringBuilder str = new StringBuilder("{");
            
            Map<TYObject, TYObject> map = TrinityNatives.cast(TYMap.class, thisObj, stackTrace).getInternalMap();
            
            for (TYObject keyObj : map.keySet()) {
                
                str.append(TrinityNatives.cast(TYString.class, keyObj.tyInvoke("toString", runtime, stackTrace, null, null), stackTrace).getInternalString());
                str.append(": ");
                str.append(TrinityNatives.cast(TYString.class, map.get(keyObj).tyInvoke("toString", runtime, stackTrace, null, null), stackTrace).getInternalString());
                str.append(", ");
            }
            
            if (map.size() > 0) {
                
                str.deleteCharAt(str.length() - 1);
                str.deleteCharAt(str.length() - 1);
            }
            str.append("}");
            
            return new TYString(str.toString());
        });
        TrinityNatives.registerMethod("Map", "length", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMapLength(TrinityNatives.cast(TYMap.class, thisObj, stackTrace)));
        TrinityNatives.registerMethod("Map", "keySet", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMapKeySet(TrinityNatives.cast(TYMap.class, thisObj, stackTrace)));
        TrinityNatives.registerMethod("Map", "values", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMapValues(TrinityNatives.cast(TYMap.class, thisObj, stackTrace)));
        TrinityNatives.registerMethod("Map", "put", false, new String[]{"key", "value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            Map<TYObject, TYObject> map = TrinityNatives.cast(TYMap.class, thisObj, stackTrace).getInternalMap();
            map.put(runtime.getVariable("key"), runtime.getVariable("value"));
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj, stackTrace));
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Map", "remove", false, new String[]{"key"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            NativeStorage.clearMapData(TrinityNatives.cast(TYMap.class, thisObj, stackTrace));
            
            return remove(TrinityNatives.cast(TYMap.class, thisObj, stackTrace), runtime.getVariable("key"), runtime, stackTrace);
        });
        TrinityNatives.registerMethod("Map", "[]", false, new String[]{"key"}, null, null, (runtime, stackTrace, thisObj, params) -> get(TrinityNatives.cast(TYMap.class, thisObj, stackTrace), runtime.getVariable("key"), runtime, stackTrace));
    }
    
    private static TYObject get(TYMap tyMap, TYObject obj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            TYBoolean equal = TrinityNatives.cast(TYBoolean.class, key.tyInvoke("==", runtime, stackTrace, null, null, obj), stackTrace);
            
            if (equal.getInternalBoolean()) {
                
                return map.get(key);
            }
        }
        
        return TYObject.NIL;
    }
    
    private static TYObject remove(TYMap tyMap, TYObject obj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            TYBoolean equal = TrinityNatives.cast(TYBoolean.class, key.tyInvoke("==", runtime, stackTrace, null, null, obj), stackTrace);
            
            if (equal.getInternalBoolean()) {
                
                return map.remove(key);
            }
        }
        
        return TYObject.NIL;
    }
}
