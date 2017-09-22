package com.github.chrisblutz.trinity.bootstrap;

import com.github.chrisblutz.trinity.info.TrinityInfo;
import com.github.chrisblutz.trinity.lang.types.nativeutils.NativeHelper;
import com.github.chrisblutz.trinity.logging.TrinityLogging;
import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.utils.FileUtils;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public class Bootstrap {
    
    public static void bootstrap() {
        
        // Load interpreter information
        TrinityInfo.loadInfo();
        
        // Start up Trinity's logger
        TrinityLogging.setup();
        TrinityLogging.logInterpreterInfo();
        
        // Check that the standard library exists
        FileUtils.checkStandardLibrary();
        
        // Load native methods
        NativeHelper.registerDefaults();
        
        // Load default library
        TrinityParser.parse(new File(FileUtils.getTrinityHome(), "lib/Object.ty"));
        TrinityParser.parse(new File(FileUtils.getTrinityHome(), "lib/"));
    }
}
