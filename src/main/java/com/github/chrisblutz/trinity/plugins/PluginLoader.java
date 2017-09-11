package com.github.chrisblutz.trinity.plugins;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.libraries.NativeLibrary;
import com.github.chrisblutz.trinity.plugins.api.Plugin;
import com.github.chrisblutz.trinity.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @author Christopher Lutz
 */
public class PluginLoader {
    
    public static final String PLUGIN_FILE = ".plugin";
    public static final String PLUGIN_FOLDER = "plugins/";
    
    public static final String JAR_EXTENSION = ".jar";
    
    public static final String PLUGIN_CLASS_PROPERTY = "pluginClass";
    public static final String NAME_PROPERTY = "name";
    public static final String VERSION_PROPERTY = "version";
    
    private static List<Plugin> plugins = new ArrayList<>();
    
    public static void loadAll() {
        
        File pluginDir = new File(FileUtils.getTrinityHome(), PLUGIN_FOLDER);
        
        if (pluginDir.exists()) {
            
            File[] jars = pluginDir.listFiles(getJarExtensionFilter());
            
            if (jars != null) {
                
                for (File jar : jars) {
                    
                    try {
                        
                        loadJar(jar);
                        
                    } catch (IOException e) {
                        
                        System.err.println("An IO error occurred while loading the plugin in file '" + jar.getName() + "'.");
                        
                        if (CLI.isDebuggingEnabled()) {
                            
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        
        // Run 'load()' on all plugins
        for (Plugin p : plugins) {
            
            p.load();
        }
    }
    
    private static FilenameFilter getJarExtensionFilter() {
        
        return (dir, name) -> name.endsWith(JAR_EXTENSION);
    }
    
    private static void loadJar(File file) throws IOException {
        
        URLClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        InputStream pluginFile = classLoader.getResourceAsStream(PLUGIN_FILE);
        
        if (pluginFile != null) {
            
            Properties properties = new Properties();
            properties.load(pluginFile);
            
            String pluginClass = properties.getProperty(PLUGIN_CLASS_PROPERTY);
            String name = properties.getProperty(NAME_PROPERTY);
            String version = properties.getProperty(VERSION_PROPERTY);
            
            if (name == null) {
                
                System.err.println("No '" + NAME_PROPERTY + "' property found for the '" + file.getName() + "' plugin.");
                return;
                
            } else if (version == null) {
                
                System.err.println("No '" + VERSION_PROPERTY + "' property found for the '" + name + "' plugin.");
                return;
                
            } else if (pluginClass == null) {
                
                System.err.println("No '" + PLUGIN_CLASS_PROPERTY + "' property found for the '" + name + "' plugin.");
                return;
            }
            
            try {
                
                Class<?> pluginCl = classLoader.loadClass(pluginClass);
                if (pluginCl.getSuperclass() == Plugin.class) {
                    
                    Plugin plugin = (Plugin) pluginCl.getConstructor().newInstance();
                    plugin.initialize(name, version);
                    
                    plugins.add(plugin);
                    
                } else {
                    
                    System.err.println("Plugin class in plugin '" + name + "' must extend " + Plugin.class.getName() + ".");
                }
                
            } catch (ClassNotFoundException e) {
                
                System.err.println("Couldn't find plugin class '" + pluginClass + "' in plugin '" + name + "'.");
                
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                
                System.err.println("An error occurred while instantiating the plugin class in plugin '" + name + "'.");
                
            } catch (NoSuchMethodException e) {
                
                System.err.println("Plugin class in plugin '" + name + "' must provide a no-argument constructor.");
            }
            
        } else {
            
            System.err.println("No '.plugin' file found for JAR file '" + PLUGIN_FOLDER + file.getName() + "'.");
        }
    }
    
    public static void triggerOnLibraryLoad(String libName, File file, NativeLibrary... libraries) {
        
        for (Plugin p : plugins) {
            
            p.onLibraryLoad(libName, file, libraries);
        }
    }
    
    public static void triggerOnFileLoad(String fileName, File file) {
        
        for (Plugin p : plugins) {
            
            p.onFileLoad(fileName, file);
        }
    }
    
    public static void triggerOnClassLoadFromFile(String fileName, File file, TYClass tyClass) {
        
        for (Plugin p : plugins) {
            
            p.onClassLoadFromFile(fileName, file, tyClass);
        }
    }
    
    public static void triggerOnClassLoad(TYClass tyClass) {
        
        for (Plugin p : plugins) {
            
            p.onClassLoad(tyClass);
        }
    }
    
    public static void triggerOnClassFinalization() {
        
        for (Plugin p : plugins) {
            
            p.onClassFinalization();
        }
    }
    
    public static void triggerOnGlobalVariableUpdate(String globalName, TYObject value) {
        
        for (Plugin p : plugins) {
            
            p.onGlobalVariableUpdate(globalName, value);
        }
    }
    
    public static void triggerOnMethodUpdate(TYClass tyClass, TYMethod tyMethod) {
        
        for (Plugin p : plugins) {
            
            p.onMethodUpdate(tyClass, tyMethod);
        }
    }
    
    public static void triggerOnErrorThrown(TYObject error) {
        
        for (Plugin p : plugins) {
            
            p.onErrorThrown(error);
        }
    }
    
    public static void triggerOnErrorCaught(TYObject error, String fileName, File file, int line) {
        
        for (Plugin p : plugins) {
            
            p.onErrorCaught(error, fileName, file, line);
        }
    }
    
    public static void unloadAll(int exitCode) {
        
        for (Plugin p : plugins) {
            
            p.unload(exitCode);
        }
    }
}
