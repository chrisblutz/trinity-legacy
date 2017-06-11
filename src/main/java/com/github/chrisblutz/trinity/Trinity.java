package com.github.chrisblutz.trinity;

import com.github.chrisblutz.trinity.bootstrap.Bootstrap;
import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.libraries.Libraries;
import com.github.chrisblutz.trinity.plugins.PluginLoader;


/**
 * Copyright 2017 Christopher Lutz
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Christopher Lutz
 */
public class Trinity {
    
    public static void main(String[] args) {
        
        CLI.parse(args);
        
        PluginLoader.loadAll();
        
        Bootstrap.bootstrap();
        
        Libraries.loadAll();
        
        CLI.start();
    }
    
    public static void exit(int result) {
        
        PluginLoader.unloadAll(result);
        
        System.exit(result);
    }
}
