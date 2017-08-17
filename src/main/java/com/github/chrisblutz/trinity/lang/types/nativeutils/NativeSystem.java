package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.natives.TrinityProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeSystem {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.System", "currentTimeMillis", (runtime, thisObj, params) -> new TYLong(System.currentTimeMillis()));
        TrinityNatives.registerMethod("Trinity.System", "getEnvironment", (runtime, thisObj, params) -> {
            
            TYObject name = runtime.getVariable("name");
            
            if (name != TYObject.NIL) {
                
                String value = System.getenv(TrinityNatives.toString(name, runtime));
                return value == null ? TYObject.NIL : new TYString(value);
                
            } else {
                
                return getEnvironmentMap();
            }
        });
        TrinityNatives.registerMethod("Trinity.System", "loadProperties", (runtime, thisObj, params) -> TrinityProperties.load());
        TrinityNatives.registerMethod("Trinity.System", "identify", (runtime, thisObj, params) -> {
            
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
