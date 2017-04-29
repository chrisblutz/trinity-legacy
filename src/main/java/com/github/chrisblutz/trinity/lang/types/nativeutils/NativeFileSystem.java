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
            
            if (object instanceof TYString) {
                
                return new TYString(new File(((TYString) object).getInternalString()).getAbsolutePath());
            }
            
            return TYObject.NIL;
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "create", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("path");
            
            if (object instanceof TYString) {
                
                try {
                    
                    File f = new File(((TYString) object).getInternalString());
                    if (f.getParentFile() != null && !f.getParentFile().exists()) {
                        
                        if (f.getParentFile().mkdirs()) {
                            
                            TYError error = new TYError("Trinity.Errors.IOError", "Unable to create parent directories.", stackTrace);
                            error.throwError();
                        }
                    }
                    return new TYBoolean(f.createNewFile());
                    
                } catch (Exception e) {
                    
                    TYError error = new TYError("Trinity.Errors.IOError", "An error occurred creating a file at '" + ((TYString) object).getInternalString() + "'.", stackTrace);
                    error.throwError();
                }
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
            
            if (path instanceof TYString && privilege instanceof TYString && append instanceof TYBoolean) {
                
                List<FilePrivilege> privileges = new ArrayList<>();
                String privilegeStr = ((TYString) privilege).getInternalString();
                if (privilegeStr.contains("r")) {
                    
                    privileges.add(FilePrivilege.READ);
                }
                if (privilegeStr.contains("w")) {
                    
                    privileges.add(FilePrivilege.WRITE);
                }
                
                FileUtils.open(((TYString) path).getInternalString(), privileges, ((TYBoolean) append).getInternalBoolean(), stackTrace);
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "read", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            
            if (path instanceof TYString) {
                
                return FileUtils.read(((TYString) path).getInternalString(), stackTrace);
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "write", true, new String[]{"path", "str"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            TYObject str = runtime.getVariable("str");
            
            if (path instanceof TYString && str instanceof TYString) {
                
                FileUtils.write(((TYString) path).getInternalString(), ((TYString) str).getInternalString(), stackTrace);
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.IO.Files.FileSystem", "close", true, new String[]{"path"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject path = runtime.getVariable("path");
            
            if (path instanceof TYString) {
                
                FileUtils.close(((TYString) path).getInternalString(), stackTrace);
            }
            
            return TYObject.NONE;
        });
    }
}
