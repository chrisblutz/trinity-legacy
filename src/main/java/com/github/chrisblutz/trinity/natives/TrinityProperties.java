package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.info.TrinityInfo;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TrinityProperties {
    
    private static Map<TYObject, TYObject> map = new HashMap<>();
    
    /**
     * Sets the Trinity system property with the specified name to the specified value.
     * <p>
     * This is the native Java equivalent of the following Trinity code:
     * <pre>
     *     System.setProperty('property', 'value')
     * </pre>
     *
     * @param property The name of the property
     * @param value    The {@code TYObject} representing the value of the property.  This may
     *                 be retrieved using {@code TrinityNatives.getObjectFor(obj)}
     */
    public static void setProperty(String property, TYObject value) {
        
        map.put(new TYString(property), value);
    }
    
    /**
     * Retrieves the Trinity system property with the specified name.
     * <p>
     * This is the native Java equivalent of the following Trinity code:
     * <pre>
     *     System.getProperty('property')
     * </pre>
     *
     * @param property The name of the property
     * @return The {@code TYObject} representing the value of the property, or {@code TYObject.NIL} if
     * the property does not exist.  (<strong>NOTE:</strong> This method may return {@code TYObject.NIL} even if a property
     * exists, if that property is set to {@code nil})
     */
    public static TYObject getProperty(String property) {
        
        return map.getOrDefault(property, TYObject.NIL);
    }
    
    /**
     * Loads Trinity's default system properties into an instance of {@code Trinity.Map}.
     * These properties include the following:
     * <pre>
     *     trinity.home
     *     trinity.name
     *     trinity.version
     *     os.arch
     *     os.name
     *     os.version
     *     user.dir
     *     user.home
     *     user.name
     * </pre>
     */
    public static TYMap load() {
        
        setProperty("trinity.name", TrinityNatives.getObjectFor(TrinityInfo.get("trinity.name")));
        setProperty("trinity.version", TrinityNatives.getObjectFor(TrinityInfo.get("trinity.version")));
        setProperty("os.arch", TrinityNatives.getObjectFor(System.getProperty("os.arch")));
        setProperty("os.name", TrinityNatives.getObjectFor(System.getProperty("os.name")));
        setProperty("os.version", TrinityNatives.getObjectFor(System.getProperty("os.version")));
        setProperty("user.dir", TrinityNatives.getObjectFor(System.getProperty("user.dir")));
        setProperty("user.home", TrinityNatives.getObjectFor(System.getProperty("user.home")));
        setProperty("user.name", TrinityNatives.getObjectFor(System.getProperty("user.name")));
        
        try {
            
            setProperty("trinity.home", TrinityNatives.getObjectFor(FileUtils.getTrinityHome().getCanonicalPath()));
            
        } catch (IOException e) {
            
            Errors.throwError("Trinity.Errors.IOError", "Unable to determine Trinity's home directory.");
        }
        
        return new TYMap(map);
    }
}
