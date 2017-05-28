package com.github.chrisblutz.trinity.libraries;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author Christopher Lutz
 */
public class Libraries {
    
    public static final String LIBRARY_DIRECTORY = "ext-libs";
    public static final String LIBRARY_EXTENSION = ".tyb";
    public static final String INTERNAL_NATIVE_DIRECTORY = "ext/";
    public static final String INTERNAL_SOURCE_DIRECTORY = "lib/";
    
    public static final String CLASS_EXTENSION = ".class";
    
    private static Map<String, File> libraryFiles = new HashMap<>();
    
    private static List<NativeLibrary> libraries = new ArrayList<>();
    
    public static void loadAll() {
        
        File dir = new File(LIBRARY_DIRECTORY);
        
        if (dir.exists() && dir.isDirectory()) {
            
            File[] files = dir.listFiles(getExtensionFilter());
            
            if (files != null) {
                
                for (File f : files) {
                    
                    load(f);
                }
            }
        }
    }
    
    public static void load(String name) {
        
        if (libraryFiles.containsKey(name.toLowerCase())) {
            
            File file = libraryFiles.get(name.toLowerCase());
            
            performLoad(name, file);
            
        } else {
            
            System.err.println("Library '" + name + "' not found.");
        }
    }
    
    private static void load(File file) {
        
        String name = file.getName();
        name = name.substring(0, name.length() - LIBRARY_EXTENSION.length());
        libraryFiles.put(name.toLowerCase(), file);
    }
    
    private static FilenameFilter getExtensionFilter() {
        
        return (dir, name) -> name.endsWith(LIBRARY_EXTENSION);
    }
    
    private static void performLoad(String name, File file) {
        
        try {
            
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            
            List<ZipEntry> natives = new ArrayList<>();
            List<ZipEntry> sources = new ArrayList<>();
            
            while (entries.hasMoreElements()) {
                
                ZipEntry entry = entries.nextElement();
                
                if (!entry.isDirectory() && entry.getName().startsWith(INTERNAL_NATIVE_DIRECTORY)) {
                    
                    natives.add(entry);
                    
                } else if (!entry.isDirectory() && entry.getName().startsWith(INTERNAL_SOURCE_DIRECTORY)) {
                    
                    sources.add(entry);
                }
            }
            
            loadNatives(name, zip, natives);
            
            for (NativeLibrary lib : libraries) {
                
                lib.initialize();
            }
            
            loadSources(name, zip, file, sources);
            
        } catch (IOException e) {
            
            System.err.println("An IO error occurred while loading library '" + name + '.');
            
            if (CLI.isDebuggingEnabled()) {
                
                e.printStackTrace();
            }
        }
    }
    
    private static void loadNatives(String libName, ZipFile file, List<ZipEntry> natives) throws IOException {
        
        List<String> names = new ArrayList<>();
        
        for (ZipEntry entry : natives) {
            
            if (entry.getName().endsWith(CLASS_EXTENSION)) {
                
                String name = entry.getName();
                name = name.substring(INTERNAL_NATIVE_DIRECTORY.length(), name.length() - CLASS_EXTENSION.length());
                name = name.replace(File.separatorChar, '/').replace('/', '.');
                
                names.add(name);
                
                InputStream is = null;
                
                try {
                    
                    is = file.getInputStream(entry);
                    File output = new File("temp-" + entry.getName());
                    if (output.getParentFile() != null) {
                        
                        output.getParentFile().mkdirs();
                    }
                    Path path = output.toPath();
                    
                    Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                    
                } catch (IOException e) {
                    
                    e.printStackTrace();
                }
                
                if (is != null) {
                    
                    is.close();
                }
            }
        }
        
        File extFiles = new File("temp-ext/");
        URL url = extFiles.toURI().toURL();
        URLClassLoader loader = URLClassLoader.newInstance(new URL[]{url});
        
        List<Class<?>> classes = new ArrayList<>();
        
        for (String name : names) {
            
            try {
                
                classes.add(loader.loadClass(name));
                
            } catch (ClassNotFoundException e) {
                
                System.err.println("Could not find class '" + name + "' inside library '" + libName + "'.");
            }
        }
        
        loader.close();
        
        // Remove temp directory
        FileUtils.delete(extFiles);
        
        for (Class<?> c : classes) {
            
            if (c.getSuperclass() == NativeLibrary.class) {
                
                try {
                    
                    NativeLibrary lib = (NativeLibrary) c.getConstructor().newInstance();
                    libraries.add(lib);
                    
                } catch (NoSuchMethodException e) {
                    
                    System.err.println("Class '" + c.getName() + "' inside library '" + libName + "' does not have a no-argument constructor.");
                    
                } catch (Exception e) {
                    
                    System.err.println("An error occurred while loading class '" + c.getName() + "' inside library '" + libName + "'.");
                    
                    if (CLI.isDebuggingEnabled()) {
                        
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    private static void loadSources(String libName, ZipFile zip, File file, List<ZipEntry> sources) {
        
        for (ZipEntry entry : sources) {
            
            if (entry.getName().endsWith(TrinityParser.SOURCE_EXTENSION)) {
                
                String filename = entry.getName();
                filename = filename.replace(File.separatorChar, '/');
                filename = filename.substring(filename.lastIndexOf('/') + 1);
                
                try {
                    
                    InputStream is = zip.getInputStream(entry);
                    TrinityParser.parse(is, filename, file);
                    is.close();
                    
                } catch (Exception e) {
                    
                    System.err.println("An error occurred while loading '" + filename + "' from library '" + libName + "'.");
                    
                    if (CLI.isDebuggingEnabled()) {
                        
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
