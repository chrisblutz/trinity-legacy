package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.interpreter.ExpressionInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.interpreter.errors.TrinityErrorException;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.io.TYNativeOutputStream;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
class NativeKernel {
    
    private static Scanner readlnSc = null;
    
    protected static void register() {
        
        TrinityNatives.registerField(TrinityNatives.Classes.KERNEL, "STDOUT", (runtime, thisObj, params) -> new TYNativeOutputStream(System.out));
        TrinityNatives.registerField(TrinityNatives.Classes.KERNEL, "STDERR", (runtime, thisObj, params) -> new TYNativeOutputStream(System.err));
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.KERNEL, "readln", (runtime, thisObj, params) -> {
            
            if (readlnSc == null) {
                
                readlnSc = new Scanner(System.in);
                
            } else {
                
                readlnSc.reset();
            }
            
            return new TYString(readlnSc.nextLine());
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.KERNEL, "throw", (runtime, thisObj, params) -> {
            
            TYObject error = runtime.getVariable("error");
            
            if (TrinityNatives.isInstance(error, TrinityNatives.Classes.ERROR)) {
                
                PluginLoader.triggerOnErrorThrown(error);
                throw new TrinityErrorException(error);
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.KERNEL, "exit", (runtime, thisObj, params) -> {
            
            Trinity.exit(TrinityNatives.toInt(runtime.getVariable("code")));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.KERNEL, "eval", (runtime, thisObj, params) -> {
            
            TYObject code = runtime.getVariable("code");
            TYObject args = runtime.getVariable("args");
            
            TYMap argsMap;
            if (args instanceof TYMap) {
                
                argsMap = (TYMap) args;
                
            } else {
                
                Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, runtime, "Kernel.eval requires its args argument to be an array.");
                argsMap = new TYMap(new HashMap<>());
            }
            
            String[] lines;
            if (code instanceof TYArray) {
                
                TYArray array = (TYArray) code;
                lines = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    
                    lines[i] = TrinityNatives.toString(array.getInternalList().get(i), runtime);
                }
                
            } else {
                
                lines = new String[]{TrinityNatives.toString(code, runtime)};
            }
            
            Block block = TrinityParser.parseStrings(new File(FileUtils.getTrinityHome(), "lib/Kernel.ty"), lines);
            ProcedureAction action = ExpressionInterpreter.interpret(block, new InterpretEnvironment(), "nil", "nil", false);
            
            TYRuntime newRuntime = new TYRuntime();
            Map<TYObject, TYObject> map = argsMap.getInternalMap();
            for (TYObject name : argsMap.getInternalMap().keySet()) {
                
                String nameStr = TrinityNatives.toString(name, runtime);
                newRuntime.setVariable(nameStr, map.get(name));
            }
            
            return action.onAction(newRuntime, TYObject.NONE);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.KERNEL, "load", (runtime, thisObj, params) -> {
            
            TYObject fileObj = runtime.getVariable("file");
            
            File file;
            if (TrinityNatives.isInstance(fileObj, "Trinity.IO.Files.File")) {
                
                String path = TrinityNatives.toString(fileObj.tyInvoke("getPath", runtime, null, null), runtime);
                file = new File(path);
                
            } else {
                
                file = new File(TrinityNatives.toString(fileObj, runtime));
            }
            
            TrinityParser.parse(file);
            
            TrinityInterpreter.runInitializationActions();
            
            return TYObject.NONE;
        });
    }
}
