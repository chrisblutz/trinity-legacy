package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.files.FilePrivilege;
import com.github.chrisblutz.trinity.files.FileUtils;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeFileSystem {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "normalize", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("path");
            
            return new TYString(new File(TrinityNatives.cast(TYString.class, object, stackTrace).getInternalString()).getAbsolutePath());
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "create", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("path");
            
            try {
                
                File f = new File(TrinityNatives.cast(TYString.class, object, stackTrace).getInternalString());
                if (f.getParentFile() != null && !f.getParentFile().exists()) {
                    
                    if (f.getParentFile().mkdirs()) {
                        
                        TYError error = new TYError("Trinity.Errors.IOError", "Unable to create parent directories.", stackTrace);
                        error.throwError();
                    }
                }
                return TYBoolean.valueFor(f.createNewFile());
                
            } catch (Exception e) {
                
                TYError error = new TYError("Trinity.Errors.IOError", "An error occurred creating a file at '" + ((TYString) object).getInternalString() + "'.", stackTrace);
                error.throwError();
            }
            
            return TYBoolean.FALSE;
        });
        Map<String, TYObject> optionalParams = new HashMap<>();
        optionalParams.put("privileges", new TYString("r"));
        optionalParams.put("append", TYBoolean.FALSE);
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "open", true, new String[]{"path"}, optionalParams, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            TYObject privilege = runtime.getVariable("privileges");
            TYObject append = runtime.getVariable("append");
            
            List<FilePrivilege> privileges = new ArrayList<>();
            String privilegeStr = TrinityNatives.cast(TYString.class, privilege, stackTrace).getInternalString();
            if (privilegeStr.contains("r")) {
                
                privileges.add(FilePrivilege.READ);
            }
            if (privilegeStr.contains("w")) {
                
                privileges.add(FilePrivilege.WRITE);
            }
            
            FileUtils.open(TrinityNatives.cast(TYString.class, path, stackTrace).getInternalString(), privileges, TrinityNatives.cast(TYBoolean.class, append, stackTrace).getInternalBoolean(), stackTrace);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "read", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            
            return FileUtils.read(TrinityNatives.cast(TYString.class, path, stackTrace).getInternalString(), stackTrace);
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "write", true, new String[]{"path", "str"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            TYObject str = runtime.getVariable("str");
            
            FileUtils.write(TrinityNatives.cast(TYString.class, path, stackTrace).getInternalString(), TrinityNatives.cast(TYString.class, str, stackTrace).getInternalString(), stackTrace);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "close", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            
            FileUtils.close(TrinityNatives.cast(TYString.class, path, stackTrace).getInternalString(), stackTrace);
            
            return TYObject.NONE;
        });
    }
}
