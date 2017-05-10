package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.TYSyntaxError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.*;


/**
 * This class houses the central API for native
 * methods in Trinity.
 *
 * @author Christopher Lutz
 */
public class TrinityNatives {
    
    private static Map<String, List<NativeAction>> pendingActions = new HashMap<>();
    private static Map<String, TYMethod> methods = new HashMap<>();
    private static Map<String, TYClass> pendingLoads = new HashMap<>();
    private static Map<String, Boolean> pendingSecure = new HashMap<>();
    private static Map<String, String> pendingLoadFiles = new HashMap<>();
    private static Map<String, Integer> pendingLoadLines = new HashMap<>();
    
    public static void registerMethod(String className, String methodName, boolean staticMethod, String[] mandatoryParams, Map<String, TYObject> optionalParams, String blockParam, ProcedureAction action) {
        
        ProcedureAction actionWithStackTrace = (runtime, stackTrace, thisObj, params) -> {
            
            TYStackTrace newTrace = stackTrace.clone();
            newTrace.add(className, methodName, null, 0);
            
            return action.onAction(runtime, newTrace, thisObj, params);
        };
        
        List<String> mandatoryParamsList;
        if (mandatoryParams != null) {
            
            mandatoryParamsList = Arrays.asList(mandatoryParams);
            
        } else {
            
            mandatoryParamsList = new ArrayList<>();
        }
        
        if (optionalParams == null) {
            
            optionalParams = new HashMap<>();
        }
        
        TYProcedure procedure = new TYProcedure(actionWithStackTrace, mandatoryParamsList, optionalParams, blockParam);
        TYMethod method = new TYMethod(methodName, staticMethod, true, ClassRegistry.getClass(className), procedure);
        String fullName = className + "." + methodName;
        methods.put(fullName, method);
    }
    
    public static void registerMethodPendingLoad(String pendingClassName, String className, String methodName, boolean staticMethod, String[] mandatoryParams, Map<String, TYObject> optionalParams, String blockParam, ProcedureAction action) {
        
        performPendingLoad(pendingClassName, () -> registerMethod(className, methodName, staticMethod, mandatoryParams, optionalParams, blockParam, action));
    }
    
    public static void performPendingLoad(String className, NativeAction action) {
        
        if (ClassRegistry.classExists(className)) {
            
            action.onAction();
            
        } else {
            
            if (!pendingActions.containsKey(className)) {
                
                pendingActions.put(className, new ArrayList<>());
            }
            
            pendingActions.get(className).add(action);
        }
    }
    
    public static void doLoad(String name, boolean secureMethod, TYClass current, String fileName, int lineNumber) {
        
        if (methods.containsKey(name)) {
            
            addToClass(name, secureMethod, current, fileName, lineNumber);
            
        } else {
            
            pendingLoads.put(name, current);
            pendingSecure.put(name, secureMethod);
            pendingLoadFiles.put(name, fileName);
            pendingLoadLines.put(name, lineNumber);
        }
    }
    
    private static void addToClass(String name, boolean secureMethod, TYClass current, String fileName, int lineNumber) {
        
        if (methods.containsKey(name)) {
            
            TYMethod method = methods.get(name);
            method.setSecureMethod(secureMethod);
            current.registerMethod(method);
            
        } else {
            
            TYSyntaxError error = new TYSyntaxError("Trinity.Errors.ParseError", "Native method " + name + " not found.", fileName, lineNumber);
            error.throwError();
        }
    }
    
    public static void triggerActionsPendingLoad(String className) {
        
        if (pendingActions.containsKey(className)) {
            
            for (NativeAction action : pendingActions.get(className)) {
                
                action.onAction();
            }
        }
        
        for (String str : pendingLoads.keySet()) {
            
            if (methods.containsKey(str)) {
                
                addToClass(str, pendingSecure.get(str), pendingLoads.get(str), pendingLoadFiles.get(str), pendingLoadLines.get(str));
                pendingLoads.remove(str);
                pendingSecure.remove(str);
                pendingLoadFiles.remove(str);
                pendingLoadLines.remove(str);
            }
        }
    }
    
    /**
     * This method wraps native types inside Trinity's
     * object format ({@code TYObject}).
     * <br>
     * <br>
     * Possible Types:<br>
     * - Integer<br>
     * - Float/Double<br>
     * - Long<br>
     * - String<br>
     * - Boolean<br>
     *
     * @param obj The object to be converted into a native Trinity object
     * @return The converted {@code TYObject} form of the original object
     */
    public static TYObject getObjectFor(Object obj) {
        
        if (obj == null) {
            
            return TYObject.NIL;
            
        } else if (obj instanceof Integer) {
            
            return new TYInt((Integer) obj);
            
        } else if (obj instanceof Float) {
            
            return new TYFloat((Float) obj);
            
        } else if (obj instanceof Double) {
            
            return new TYFloat((Double) obj);
            
        } else if (obj instanceof Long) {
            
            return new TYLong((Long) obj);
            
        } else if (obj instanceof String) {
            
            return new TYString((String) obj);
            
        } else if (obj instanceof Boolean) {
            
            if ((Boolean) obj) {
                
                return TYBoolean.TRUE;
                
            } else {
                
                return TYBoolean.FALSE;
            }
        }
        
        TYSyntaxError error = new TYSyntaxError("Trinity.Errors.NativeTypeError", "Trinity does not have native type-conversion utilities for " + obj.getClass() + ".", null, 0);
        error.throwError();
        
        return TYObject.NIL;
    }
    
    
    /**
     * This method wraps an array of native types inside Trinity's
     * array format ({@code TYObject}).  All objects inside are converted
     * into Trinity's native type of them.
     * <br>
     * <br>
     * Possible Types:<br>
     * - Integer<br>
     * - Float/Double<br>
     * - Long<br>
     * - String<br>
     * - Boolean<br>
     *
     * @param arr The array of Objects to be converted into a native array of Trinity object
     * @return The converted {@code TYArray} form of the original form, containing {@code TYObject} forms of
     * the original objects
     */
    public static TYArray getArrayFor(Object[] arr) {
        
        List<TYObject> objects = new ArrayList<>();
        
        for (Object o : arr) {
            
            objects.add(getObjectFor(o));
        }
        
        return new TYArray(objects);
    }
    
    public static TYObject newInstance(String className, TYObject... args) {
        
        return newInstance(className, new TYRuntime(), new TYStackTrace(), args);
    }
    
    public static TYObject newInstance(String className, TYRuntime runtime, TYStackTrace stackTrace, TYObject... args) {
        
        return ClassRegistry.getClass(className).tyInvoke("new", runtime, stackTrace, null, null, TYObject.NONE, args);
    }
    
    public static <T extends TYObject> T cast(Class<T> desiredClass, TYObject object, TYStackTrace stackTrace) {
        
        if (desiredClass.isInstance(object)) {
            
            return desiredClass.cast(object);
            
        } else {
            
            TYError error = new TYError("Trinity.Errors.InvalidTypeError", "Unexpected value of type " + object.getObjectClass().getName() + " found.", stackTrace);
            error.throwError();
            
            // This will throw an error, but the program will exit at the line above, never reaching this point
            return desiredClass.cast(object);
        }
    }
}
