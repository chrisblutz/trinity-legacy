package com.github.chrisblutz.trinity.libraries.compiling;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.libraries.Libraries;
import com.github.chrisblutz.trinity.utils.FileUtils;

import javax.tools.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author Christopher Lutz
 */
public class LibraryCompiler {
    
    public static final String TYBUNDLE_FILE = ".tybundle";
    
    public static final String TEMPORARY_COMPILE_DIRECTORY = "temp-out/";
    public static final String OUTPUT_DIRECTORY = "out/";
    
    public static final String NATIVE_SOURCES_PROPERTY = "nativeSource";
    public static final String NATIVE_RESOURCES_PROPERTY = "nativeResource";
    public static final String TRINITY_SOURCES_PROPERTY = "source";
    public static final String LIBRARY_NAME_PROPERTY = "name";
    public static final String LIBRARY_VERSION_PROPERTY = "version";
    
    public static final String JAVA_SOURCE_EXTENSION = ".java";
    
    public static void main(String[] args) {
        
        compile();
    }
    
    public static void compile() {
        
        if (!checkSystemEnvironment()) {
            
            System.err.println("Environment variable TRINITY_HOME not found.  It should contain the home directory of your Trinity installation.");
            return;
        }
        
        File bundleFile = new File(TYBUNDLE_FILE);
        
        if (!bundleFile.exists()) {
            
            System.err.println("No " + TYBUNDLE_FILE + " file found in the current location.");
            return;
        }
        
        Properties properties = new Properties();
        
        try {
            
            FileInputStream inputStream = new FileInputStream(bundleFile);
            properties.load(inputStream);
            inputStream.close();
            
        } catch (IOException e) {
            
            System.err.println("An error occurred while reading the " + TYBUNDLE_FILE + " file.");
            
            if (CLI.isDebuggingEnabled()) {
                
                e.printStackTrace();
            }
        }
        
        String libName = properties.getProperty(LIBRARY_NAME_PROPERTY);
        String version = properties.getProperty(LIBRARY_VERSION_PROPERTY);
        String nativeSource = properties.getProperty(NATIVE_SOURCES_PROPERTY);
        String nativeResource = properties.getProperty(NATIVE_RESOURCES_PROPERTY);
        String source = properties.getProperty(TRINITY_SOURCES_PROPERTY);
        
        if (libName == null) {
            
            System.err.println(TYBUNDLE_FILE + " must declare a " + LIBRARY_NAME_PROPERTY + " property.");
            return;
            
        } else if (version == null) {
            
            System.err.println(TYBUNDLE_FILE + " must declare a " + LIBRARY_VERSION_PROPERTY + " property.");
            return;
        }
        
        List<File> nativeFiles = new ArrayList<>();
        List<String> nativeNames = new ArrayList<>();
        List<File> sourceFiles = new ArrayList<>();
        List<String> sourceNames = new ArrayList<>();
        
        if (nativeSource != null) {
            
            File nativeSourceDir = new File(nativeSource);
            
            if (!nativeSourceDir.exists()) {
                
                System.err.println("Native source directory " + nativeSource + " does not exist.");
                return;
            }
            
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            
            if (compiler == null) {
                
                System.err.println("No Java compiler found.  Make sure a JDK is installed.");
                return;
            }
            
            try {
                
                compileNativeSources(compiler, nativeSourceDir);
                
            } catch (IOException e) {
                
                System.err.println("An IO exception occurred while bundling this library.");
                
                if (CLI.isDebuggingEnabled()) {
                    
                    e.printStackTrace();
                }
            }
            
            File tempDir = new File(TEMPORARY_COMPILE_DIRECTORY);
            collectOnlyFiles(nativeFiles, nativeNames, tempDir, tempDir.getAbsolutePath());
        }
        
        if (nativeResource != null) {
            
            File nativeResourceDir = new File(nativeResource);
            
            if (!nativeResourceDir.exists()) {
                
                System.err.println("Native resource directory " + nativeSource + " does not exist.");
                return;
            }
            
            collectOnlyFiles(nativeFiles, nativeNames, nativeResourceDir, nativeResourceDir.getAbsolutePath());
        }
        
        if (source != null) {
            
            File sourceDir = new File(source);
            
            if (!sourceDir.exists()) {
                
                System.err.println("Source directory " + source + " does not exist.");
                return;
            }
            
            collectOnlyFiles(sourceFiles, sourceNames, sourceDir, sourceDir.getAbsolutePath());
        }
        
        try {
            
            File output = new File(OUTPUT_DIRECTORY);
            output.mkdirs();
            
            File tybFile = new File(output, libName + "-" + version + Libraries.LIBRARY_EXTENSION);
            writeBundle(tybFile, nativeFiles, nativeNames, sourceFiles, sourceNames);
            
            System.out.println("Generated '" + tybFile.getName() + "' successfully.");
            
        } catch (IOException e) {
            
            System.err.println("An IO error occurred while writing the library to a bundle.");
            
            if (CLI.isDebuggingEnabled()) {
                
                e.printStackTrace();
            }
        }
        
        // Remove temporary compilation directory
        FileUtils.delete(new File(TEMPORARY_COMPILE_DIRECTORY));
    }
    
    private static void compileNativeSources(JavaCompiler compiler, File source) throws IOException {
        
        File tempDir = new File(TEMPORARY_COMPILE_DIRECTORY);
        copySources(source, tempDir, source.getAbsolutePath());
        
        List<File> sourceFiles = new ArrayList<>();
        File[] files = tempDir.listFiles();
        if (files != null) {
            
            for (File f : files) {
                
                generateListOfSources(sourceFiles, f);
            }
            
            if (sourceFiles.size() > 0) {
                
                List<String> args = new ArrayList<>();
                args.add("-g");
                
                DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
                StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
                Iterable<? extends JavaFileObject> compilationUnits = manager.getJavaFileObjects(sourceFiles.toArray(new File[sourceFiles.size()]));
                
                JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnosticCollector, args, null, compilationUnits);
                
                boolean result = task.call();
                
                if (!result) {
                    
                    System.err.println("Compilation errors occurred during building.");
                    
                    for (Diagnostic<? extends JavaFileObject> d : diagnosticCollector.getDiagnostics()) {
                        
                        CompileException exception = new CompileException(d.getSource().getName(), d.getLineNumber(), d.getMessage(null));
                        exception.printStackTrace();
                    }
                }
                
                // Remove temporary source files (we don't want to bundle the .java files)
                for (File f : sourceFiles) {
                    
                    f.delete();
                }
            }
        }
    }
    
    private static void copySources(File current, File target, String sourcePath) throws IOException {
        
        if (current.isDirectory()) {
            
            String targetDir = getFileNameWithoutPath(current.getAbsolutePath(), sourcePath);
            new File(target, targetDir).mkdirs();
            
            File[] files = current.listFiles();
            
            if (files != null) {
                
                for (File f : files) {
                    
                    if (f.isDirectory()) {
                        
                        copySources(f, target, sourcePath);
                        
                    } else {
                        
                        String targetFile = getFileNameWithoutPath(f.getAbsolutePath(), sourcePath);
                        Files.copy(f.toPath(), new File(target, targetFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }
    
    private static void generateListOfSources(List<File> sourceFiles, File current) {
        
        if (current.isDirectory()) {
            
            File[] files = current.listFiles();
            
            if (files != null) {
                
                for (File f : files) {
                    
                    generateListOfSources(sourceFiles, f);
                }
            }
            
        } else if (current.getName().endsWith(JAVA_SOURCE_EXTENSION)) {
            
            sourceFiles.add(current);
        }
    }
    
    private static void collectOnlyFiles(List<File> files, List<String> names, File current, String origPath) {
        
        if (current.isDirectory()) {
            
            File[] fileArr = current.listFiles();
            
            if (fileArr != null) {
                
                for (File f : fileArr) {
                    
                    collectOnlyFiles(files, names, f, origPath);
                }
            }
            
        } else {
            
            files.add(current);
            String name = getFileNameWithoutPath(current.getAbsolutePath(), origPath);
            names.add(name);
        }
    }
    
    private static String getFileNameWithoutPath(String fullPath, String cutPath) {
        
        String path = cutPath;
        if (!path.replace(File.separatorChar, '/').endsWith("/")) {
            
            path = cutPath + "/";
        }
        
        if (path.length() <= fullPath.length()) {
            
            return fullPath.substring(path.length());
            
        } else {
            
            return "";
        }
    }
    
    private static void writeBundle(File bundle, List<File> nativeFiles, List<String> nativeNames, List<File> sourceFiles, List<String> sourceNames) throws IOException {
        
        if (bundle.exists()) {
            
            bundle.delete();
        }
        
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(bundle));
        
        if (nativeFiles.size() > 0) {
            
            for (int i = 0; i < nativeFiles.size() && i < nativeNames.size(); i++) {
                
                File file = nativeFiles.get(i);
                String name = nativeNames.get(i);
                
                ZipEntry entry = new ZipEntry(Libraries.INTERNAL_NATIVE_DIRECTORY + name);
                out.putNextEntry(entry);
                
                Files.copy(file.toPath(), out);
            }
        }
        
        if (sourceFiles.size() > 0) {
            
            for (int i = 0; i < sourceFiles.size() && i < sourceNames.size(); i++) {
                
                File file = sourceFiles.get(i);
                String name = sourceNames.get(i);
                
                ZipEntry entry = new ZipEntry(Libraries.INTERNAL_SOURCE_DIRECTORY + name);
                out.putNextEntry(entry);
                
                Files.copy(file.toPath(), out);
            }
        }
        
        out.close();
    }
    
    private static boolean checkSystemEnvironment() {
        
        return System.getenv().containsKey("TRINITY_HOME");
    }
}
