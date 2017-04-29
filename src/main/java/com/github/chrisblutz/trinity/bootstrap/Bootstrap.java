package com.github.chrisblutz.trinity.bootstrap;

import com.github.chrisblutz.trinity.info.TrinityInfo;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.nativeutils.NativeHelper;
import com.github.chrisblutz.trinity.parser.TrinityParser;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class Bootstrap {
    
    public static void bootstrap() {
        
        // Load interpreter information
        TrinityInfo.loadInfo();
        
        // Load native methods
        NativeHelper.registerDefaults();
        
        // Load default library
        TrinityParser.parse(new File("lib/Object.ty"));
        TrinityParser.parse(new File("lib/"));
        
        // Load system properties
        ClassRegistry.getClass("System").tyInvoke("loadProperties", new TYRuntime(), new TYStackTrace(), null, null, TYObject.NIL);
    }
    
    public static void bootstrapUI() {
        
        // Load UI library
        TrinityParser.parse(new File("lib-ui/"));
    }
}
