package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.files.FilePrivilege;
import com.github.chrisblutz.trinity.files.FileUtils;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
class NativeFileSystem {
    
    static void register(Map<String, TYMethod> methods) {
        
        methods.put("Trinity.IO.Files.FileSystem.normalize", new TYMethod("normalize", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Trinity.IO.Files.FileSystem", "normalize");
            
            if (params.length > 0) {
                
                TYObject object = params[0];
                
                if (object instanceof TYString) {
                    
                    return new TYString(new File(((TYString) object).getInternalString()).getAbsolutePath());
                }
            }
            
            return TYObject.NIL;
        })));
        methods.put("Trinity.IO.Files.FileSystem.create", new TYMethod("create", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Trinity.IO.Files.FileSystem", "create");
            
            if (params.length > 0) {
                
                TYObject object = params[0];
                
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
            }
            
            return TYObject.NIL;
        })));
        methods.put("Trinity.IO.Files.FileSystem.open", new TYMethod("open", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Trinity.IO.Files.FileSystem", "open");
            
            if (params.length > 1) {
                
                TYObject path = params[0];
                TYObject privilege = params[1];
                TYObject append = params[2];
                
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
            }
            
            return TYObject.NONE;
        })));
        methods.put("Trinity.IO.Files.FileSystem.read", new TYMethod("read", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Trinity.IO.Files.FileSystem", "read");
            
            if (params.length > 0) {
                
                TYObject path = params[0];
                
                if (path instanceof TYString) {
                    
                    return FileUtils.read(((TYString) path).getInternalString(), stackTrace);
                }
            }
            
            return TYObject.NONE;
        })));
        methods.put("Trinity.IO.Files.FileSystem.write", new TYMethod("write", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Trinity.IO.Files.FileSystem", "write");
            
            if (params.length > 1) {
                
                TYObject path = params[0];
                TYObject str = params[1];
                
                if (path instanceof TYString && str instanceof TYString) {
                    
                    FileUtils.write(((TYString) path).getInternalString(), ((TYString) str).getInternalString(), stackTrace);
                }
            }
            
            return TYObject.NONE;
        })));
        methods.put("Trinity.IO.Files.FileSystem.close", new TYMethod("close", true, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            NativeHelper.appendToStackTrace(stackTrace, "Trinity.IO.Files.FileSystem", "close");
            
            if (params.length > 0) {
                
                TYObject path = params[0];
                
                if (path instanceof TYString) {
                    
                    FileUtils.close(((TYString) path).getInternalString(), stackTrace);
                }
            }
            
            return TYObject.NONE;
        })));
    }
}
