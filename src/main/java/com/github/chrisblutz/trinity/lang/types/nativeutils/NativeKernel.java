package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.interpreter.ExpressionInterpreter;
import com.github.chrisblutz.trinity.interpreter.InterpretEnvironment;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.io.TYNativeOutputStream;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.parser.blocks.Block;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * @author Christopher Lutz
 */
class NativeKernel {
    
    private static Scanner readlnSc = null;
    
    static void register() {
        
        TrinityNatives.registerGlobalPendingLoad("Trinity.IO.NativeOutputStream", "STDOUT", (runtime, thisObj, params) -> new TYNativeOutputStream(System.out));
        TrinityNatives.registerGlobalPendingLoad("Trinity.IO.NativeOutputStream", "STDERR", (runtime, thisObj, params) -> new TYNativeOutputStream(System.err));
        
        TrinityNatives.registerMethod("Kernel", "readln", true, null, null, null, (runtime, thisObj, params) -> {
            
            if (readlnSc == null) {
                
                readlnSc = new Scanner(System.in);
                
            } else {
                
                readlnSc.reset();
            }
            
            return new TYString(readlnSc.nextLine());
        });
        TrinityNatives.registerMethod("Kernel", "throw", true, new String[]{"error"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject error = runtime.getVariable("error");
            
            if (error.getObjectClass().isInstanceOf(ClassRegistry.getClass("Trinity.Errors.Error"))) {
                
                String errorMessage = TrinityNatives.cast(TYString.class, error.tyInvoke("toString", runtime, null, null)).getInternalString();
                System.err.println(errorMessage);
                
                Trinity.exit(1);
                
                // TODO allow for catching errors
            }
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Kernel", "exit", true, new String[]{"code"}, null, null, (runtime, thisObj, params) -> {
            
            Trinity.exit(TrinityNatives.toInt(runtime.getVariable("code")));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Kernel", "sleep", true, new String[]{"millis"}, null, null, (runtime, thisObj, params) -> {
            
            long wait = TrinityNatives.toLong(runtime.getVariable("millis"));
            
            try {
                
                Thread.sleep(wait);
                
            } catch (InterruptedException e) {
                
                // TODO
            }
            
            return TYObject.NONE;
        });
        Map<String, ProcedureAction> optionalParameters = new HashMap<>();
        optionalParameters.put("args", (runtime, thisObj, params) -> new TYMap(new HashMap<>()));
        TrinityNatives.registerMethod("Kernel", "eval", true, new String[]{"code"}, optionalParameters, null, (runtime, thisObj, params) -> {
            
            TYObject code = runtime.getVariable("code");
            TYObject args = runtime.getVariable("args");
            
            TYMap argsMap;
            if (args instanceof TYMap) {
                
                argsMap = (TYMap) args;
                
            } else {
                
                Errors.throwError("Trinity.Errors.InvalidTypeError", "Kernel.eval requires its args argument to be an array.", runtime);
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
            
            Block block = TrinityParser.parseStrings(new File("lib/Kernel.ty"), lines);
            ProcedureAction action = ExpressionInterpreter.interpret(block, new InterpretEnvironment(), "nil", "nil", false);
            
            TYRuntime newRuntime = new TYRuntime();
            Map<TYObject, TYObject> map = argsMap.getInternalMap();
            for (TYObject name : argsMap.getInternalMap().keySet()) {
                
                String nameStr = TrinityNatives.toString(name, runtime);
                newRuntime.setVariable(nameStr, map.get(name));
            }
            
            return action.onAction(newRuntime, TYObject.NONE);
        });
    }
}
