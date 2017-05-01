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
            
            Map<TYObject, TYObject> map = ((TYMap) thisObj).getInternalMap();
            
            for (TYObject keyObj : map.keySet()) {
                
                str.append(((TYString) keyObj.tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString());
                str.append(": ");
                str.append(((TYString) map.get(keyObj).tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString());
                str.append(", ");
            }
            
            if (map.size() > 0) {
                
                str.deleteCharAt(str.length() - 1);
                str.deleteCharAt(str.length() - 1);
            }
            str.append("}");
            
            return new TYString(str.toString());
        });
        TrinityNatives.registerMethod("Map", "length", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMapLength((TYMap) thisObj));
        TrinityNatives.registerMethod("Map", "keySet", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMapKeySet((TYMap) thisObj));
        TrinityNatives.registerMethod("Map", "values", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getMapValues((TYMap) thisObj));
        TrinityNatives.registerMethod("Map", "put", false, new String[]{"key", "value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            Map<TYObject, TYObject> map = ((TYMap) thisObj).getInternalMap();
            map.put(runtime.getVariable("key"), runtime.getVariable("value"));
            
            NativeStorage.clearMapData((TYMap) thisObj);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Map", "remove", false, new String[]{"key"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            NativeStorage.clearMapData((TYMap) thisObj);
            
            return remove((TYMap) thisObj, runtime.getVariable("key"), runtime, stackTrace);
        });
        TrinityNatives.registerMethod("Map", "[]", false, new String[]{"key"}, null, null, (runtime, stackTrace, thisObj, params) -> get((TYMap) thisObj, runtime.getVariable("key"), runtime, stackTrace));
    }
    
    private static TYObject get(TYMap tyMap, TYObject obj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            TYBoolean equal = (TYBoolean) key.tyInvoke("==", runtime, stackTrace, null, null, obj);
            
            if (equal.getInternalBoolean()) {
                
                return map.get(key);
            }
        }
        
        return TYObject.NIL;
    }
    
    private static TYObject remove(TYMap tyMap, TYObject obj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        Map<TYObject, TYObject> map = tyMap.getInternalMap();
        
        for (TYObject key : map.keySet()) {
            
            TYBoolean equal = (TYBoolean) key.tyInvoke("==", runtime, stackTrace, null, null, obj);
            
            if (equal.getInternalBoolean()) {
                
                return map.remove(key);
            }
        }
        
        return TYObject.NIL;
    }
}
