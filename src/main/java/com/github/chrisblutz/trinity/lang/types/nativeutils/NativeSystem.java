package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.info.TrinityInfo;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeSystem {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.System", "currentTimeMillis", true, null, null, null, (runtime, thisObj, params) -> new TYLong(System.currentTimeMillis()));
        Map<String, ProcedureAction> optionalParams = new HashMap<>();
        optionalParams.put("name", (runtime, thisObj, params) -> TYObject.NIL);
        TrinityNatives.registerMethod("Trinity.System", "getEnvironment", true, null, optionalParams, null, (runtime, thisObj, params) -> {
            
            TYObject name = runtime.getVariable("name");
            
            if (name != TYObject.NIL) {
                
                String value = System.getenv(TrinityNatives.toString(name, runtime));
                return value == null ? TYObject.NIL : new TYString(value);
                
            } else {
                
                return getEnvironmentMap();
            }
        });
        TrinityNatives.registerMethod("Trinity.System", "loadProperties", true, null, null, null, (runtime, thisObj, params) -> {
            
            Map<TYObject, TYObject> map = new HashMap<>();
            
            map.put(new TYString("trinity.name"), TrinityNatives.getObjectFor(TrinityInfo.get("trinity.name")));
            map.put(new TYString("trinity.version"), TrinityNatives.getObjectFor(TrinityInfo.get("trinity.version")));
            map.put(new TYString("os.arch"), TrinityNatives.getObjectFor(System.getProperty("os.arch")));
            map.put(new TYString("os.name"), TrinityNatives.getObjectFor(System.getProperty("os.name")));
            map.put(new TYString("os.version"), TrinityNatives.getObjectFor(System.getProperty("os.version")));
            map.put(new TYString("user.dir"), TrinityNatives.getObjectFor(System.getProperty("user.dir")));
            map.put(new TYString("user.home"), TrinityNatives.getObjectFor(System.getProperty("user.home")));
            map.put(new TYString("user.name"), TrinityNatives.getObjectFor(System.getProperty("user.name")));
            
            ClassRegistry.getClass("Trinity.System").setVariable("properties", new TYMap(map));
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.System", "identify", true, new String[]{"obj"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("obj");
            return NativeStorage.getHashCode(object);
        });
    }
    
    private static TYMap environmentMap = null;
    
    private static TYMap getEnvironmentMap() {
        
        if (environmentMap == null) {
            
            Map<TYObject, TYObject> map = new HashMap<>();
            for (String s : System.getenv().keySet()) {
                
                String value = System.getenv(s);
                if (value == null) {
                    
                    map.put(new TYString(s), TYObject.NIL);
                    
                } else {
                    
                    map.put(new TYString(s), new TYString(System.getenv(s)));
                }
            }
            
            environmentMap = new TYMap(map);
        }
        
        return environmentMap;
    }
}
