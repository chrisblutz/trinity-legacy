package com.github.chrisblutz.trinity.files;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
public class FileUtils {
    
    private static Map<String, StringBuilder> fileContents = new HashMap<>();
    private static Map<String, List<FilePrivilege>> filePrivileges = new HashMap<>();
    private static Map<String, Boolean> fileAppending = new HashMap<>();
    private static Map<String, Boolean> hasWritten = new HashMap<>();
    
    public static void open(String fileName, List<FilePrivilege> privileges, boolean append, TYStackTrace stackTrace) {
        
        StringBuilder sb = new StringBuilder("");
        try {
            
            Scanner sc = new Scanner(new File(fileName));
            while (sc.hasNextLine()) {
                
                sb.append(sc.nextLine()).append("\n");
            }
            sc.close();
            
            fileContents.put(fileName, sb);
            filePrivileges.put(fileName, privileges);
            fileAppending.put(fileName, append);
            hasWritten.put(fileName, false);
            
        } catch (FileNotFoundException e) {
            
            TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not found.", stackTrace);
            error.throwError();
        }
    }
    
    public static TYObject read(String fileName, TYStackTrace stackTrace) {
        
        if (filePrivileges.containsKey(fileName)) {
            
            if (filePrivileges.get(fileName).contains(FilePrivilege.READ)) {
                
                StringBuilder sb = fileContents.get(fileName);
                return new TYString(sb.toString());
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not open with reading privileges.", stackTrace);
                error.throwError();
            }
            
        } else {
            
            TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not open.", stackTrace);
            error.throwError();
        }
        
        return TYObject.NIL;
    }
    
    public static void write(String fileName, String str, TYStackTrace stackTrace) {
        
        if (filePrivileges.containsKey(fileName)) {
            
            if (filePrivileges.get(fileName).contains(FilePrivilege.WRITE)) {
                
                StringBuilder sb = fileContents.get(fileName);
                
                if (fileAppending.get(fileName) || hasWritten.get(fileName)) {
                    
                    sb.append(str);
                    
                } else {
                    
                    fileContents.put(fileName, new StringBuilder(str));
                    hasWritten.put(fileName, true);
                }
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not open with writing privileges.", stackTrace);
                error.throwError();
            }
            
        } else {
            
            TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not open.", stackTrace);
            error.throwError();
        }
    }
    
    public static void close(String fileName, TYStackTrace stackTrace) {
        
        if (filePrivileges.containsKey(fileName)) {
            
            StringBuilder sb = fileContents.get(fileName);
            if (filePrivileges.get(fileName).contains(FilePrivilege.WRITE)) {
                
                try {
                    
                    PrintStream ps = new PrintStream(new File(fileName));
                    ps.println(sb.toString());
                    ps.close();
                    
                } catch (FileNotFoundException e) {
                    
                    TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not found.", stackTrace);
                    error.throwError();
                }
            }
            
            fileContents.remove(fileName);
            filePrivileges.remove(fileName);
            fileAppending.remove(fileName);
            hasWritten.remove(fileName);
            
        } else {
            
            TYError error = new TYError("Trinity.Errors.IOError", "File '" + fileName + "' not open.", stackTrace);
            error.throwError();
        }
    }
}
