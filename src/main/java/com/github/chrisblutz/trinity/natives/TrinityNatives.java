package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.interpreter.Scope;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TrinityStack;
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
 * This class houses the central API for native methods and global variables in Trinity.
 * It also provides utility methods for object construction, method invocation, and native
 * type conversion.
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
    private static Map<String, Scope> pendingScope = new HashMap<>();
    private static Map<String, String[]> pendingLeadingComments = new HashMap<>();
    
    private static Map<String, ProcedureAction> globals = new HashMap<>();
    
    /**
     * Registers a native method to be loaded when its container class is loaded.<br>
     * <br>
     * This method allows for the definition of argument names, which can be retrieved using
     * {@code Runtime.getVariable()}:
     * <pre>
     *     runtime.getVariable("name");
     * </pre>
     * <p>
     * If there is a block argument defined, this can also be retrieved using this same method.
     * The resulting {@code TYObject} will be an instance of the Trinity class {@code Procedure}.
     * It can be invoked by using {@code TrinityNatives.call()}:
     * <pre>
     *      TYObject procedure = runtime.getVariable("blockArg")
     *      TYObject result = TrinityNatives.call(procedure, "call", runtime, [any parameters]);
     * </pre>
     *
     * @param className       The name of the container class for this method
     * @param methodName      The name of this method
     * @param staticMethod    Whether or not this method is defined as {@code static}
     * @param mandatoryParams The mandatory argument names for this method.  This may be {@code null} if there are no mandatory arguments.
     * @param optionalParams  The optional arguments for this method, defined in the map with the name as the key
     *                        and a {@code ProcedureAction} that returns the default argument value as the value.  This may be {@code null}
     *                        if there are no optional arguments.
     * @param blockParam      The block argument for this method.  This is the equivalent of defining an {@code &block} argument in Trinity
     *                        source code.  This may be {@code null} if there is no block argument.
     * @param overflowParam   The overflow argument for this method.  If any additional arguments are passed to this method beyond the specified
     *                        mandatory, optional, and block arguments, they will be placed in an array and passed in as this overflow argument.
     *                        This is the equivalent of defining an {@code ...args} argument in Trinity source code.  This may be {@code null}
     *                        if there is no overflow argument.
     * @param action          The {@code ProcedureAction} that is called when this method is invoked.  The return value from this action is used as
     *                        the method's return value.
     */
    public static void registerMethod(String className, String methodName, boolean staticMethod, String[] mandatoryParams, Map<String, ProcedureAction> optionalParams, String blockParam, String overflowParam, ProcedureAction action) {
        
        ProcedureAction actionWithStackTrace = (runtime, thisObj, params) -> {
            
            TrinityStack.add(className, methodName, null, 0);
            
            TYObject result = action.onAction(runtime, thisObj, params);
            
            TrinityStack.pop();
            
            return result;
        };
        
        List<String> mandatoryParamsList;
        if (mandatoryParams != null) {
            
            mandatoryParamsList = Arrays.asList(mandatoryParams);
            
        } else {
            
            mandatoryParamsList = new ArrayList<>();
        }
        
        if (optionalParams == null) {
            
            optionalParams = new TreeMap<>();
        }
        
        TYProcedure procedure = new TYProcedure(actionWithStackTrace, mandatoryParamsList, optionalParams, blockParam, overflowParam, true);
        TYMethod method = new TYMethod(methodName, staticMethod, true, ClassRegistry.getClass(className), procedure);
        String fullName = className + "." + methodName;
        methods.put(fullName, method);
    }
    
    /**
     * Registers a global variable.  Variables declared using this method are not automatically
     * available inside Trinity.  In order to maintain source code transparency and avoid confusion
     * with globals declared invisibly, all globals declared using this method must be initialized
     * inside Trinity source code using {@code Natives.initGlobalVariable()}:
     * <pre>
     *     Natives.initGlobalVariable('name')
     * </pre>
     * <p>
     * It is recommended that this call come in the initializer code for the file, before any
     * class or module definitions.
     *
     * @param name   The name of this variable (will be used in source code as {@code $name}.
     * @param action The {@code ProcedureAction} that returns the value of this global.
     */
    public static void registerGlobal(String name, ProcedureAction action) {
        
        globals.put(name, action);
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
    
    public static void doLoad(String name, boolean secureMethod, TYClass current, String fileName, int lineNumber, Scope scope, String[] leadingComments) {
        
        if (methods.containsKey(name)) {
            
            addToClass(name, secureMethod, current, fileName, lineNumber, scope, leadingComments);
            
        } else {
            
            pendingLoads.put(name, current);
            pendingSecure.put(name, secureMethod);
            pendingLoadFiles.put(name, fileName);
            pendingLoadLines.put(name, lineNumber);
            pendingScope.put(name, scope);
            pendingLeadingComments.put(name, leadingComments);
        }
    }
    
    private static void addToClass(String name, boolean secureMethod, TYClass current, String fileName, int lineNumber, Scope scope, String[] leadingComments) {
        
        if (methods.containsKey(name)) {
            
            TYMethod method = methods.get(name);
            method.setScope(scope);
            method.setLeadingComments(leadingComments);
            method.setSecureMethod(secureMethod);
            current.registerMethod(method);
            
        } else {
            
            Errors.throwSyntaxError("Trinity.Errors.ParseError", "Native method " + name + " not found.", fileName, lineNumber);
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
                
                addToClass(str, pendingSecure.get(str), pendingLoads.get(str), pendingLoadFiles.get(str), pendingLoadLines.get(str), pendingScope.get(str), pendingLeadingComments.get(str));
                pendingLoads.remove(str);
                pendingSecure.remove(str);
                pendingLoadFiles.remove(str);
                pendingLoadLines.remove(str);
                pendingScope.remove(str);
                pendingLeadingComments.remove(str);
            }
        }
    }
    
    public static void checkAllLoaded() {
        
        for (String str : pendingLoads.keySet()) {
            
            Errors.throwSyntaxErrorDelayExit("Trinity.Errors.ParseError", "Native method " + str + " not implemented.", pendingLoadFiles.get(str), pendingLoadLines.get(str));
        }
        
        if (pendingLoads.size() > 0) {
            
            Errors.exit();
        }
    }
    
    public static ProcedureAction getGlobalProcedureAction(String name) {
        
        return globals.get(name);
    }
    
    /**
     * This method wraps Java objects inside Trinity's
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
            
        } else if (obj.getClass().isArray()) {
            
            return getArrayFor((Object[]) obj);
        }
        
        Errors.throwSyntaxError("Trinity.Errors.NativeTypeError", "Trinity does not have native type-conversion utilities for " + obj.getClass() + ".", null, 0);
        
        return TYObject.NIL;
    }
    
    
    /**
     * This method wraps an array of Java objects inside Trinity's
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
    
    /**
     * Creates a new instance of the specified Trinity class and passes the specified arguments to the
     * class's constructor.<br>
     * <br>
     * This method is the equivalent of calling:
     * <pre>
     *     TrinityNatives.newInstance(className, new TYRuntime(), args);
     * </pre>
     *
     * @param className The name of the Trinity class to instantiate
     * @param args      Arguments to pass to the class's constructor
     * @return The constructed {@code TYObject} representing the new instance
     */
    public static TYObject newInstance(String className, TYObject... args) {
        
        return newInstance(className, new TYRuntime(), args);
    }
    
    /**
     * Creates a new instance of the specified Trinity class and passes the specified arguments to the
     * class's constructor.
     *
     * @param className The name of the Trinity class to instantiate
     * @param args      Arguments to pass to the class's constructor
     * @param runtime   The {@code TYRuntime} instance representing the runtime state in which this instance was created
     * @return The constructed {@code TYObject} representing the new instance
     */
    public static TYObject newInstance(String className, TYRuntime runtime, TYObject... args) {
        
        return ClassRegistry.getClass(className).tyInvoke("new", runtime, null, null, TYObject.NONE, args);
    }
    
    public static TYObject call(TYObject thisObj, String methodName, TYRuntime runtime, TYObject... params) {
        
        return call(thisObj.getObjectClass().getName(), methodName, runtime, thisObj, params);
    }
    
    public static TYObject call(String className, String methodName, TYRuntime runtime, TYObject thisObj, TYObject... args) {
        
        if (ClassRegistry.classExists(className)) {
            
            return ClassRegistry.getClass(className).tyInvoke(methodName, runtime, null, null, thisObj, args);
            
        } else {
            
            Errors.throwError("Trinity.Errors.ClassNotFoundError", runtime, "Class " + className + " does not exist.");
        }
        
        return TYObject.NIL;
    }
    
    public static <T extends TYObject> T cast(Class<T> desiredClass, TYObject object) {
        
        if (desiredClass.isInstance(object)) {
            
            return desiredClass.cast(object);
            
        } else {
            
            Errors.throwError("Trinity.Errors.InvalidTypeError", "Unexpected value of type " + object.getObjectClass().getName() + " found.");
            
            // This will throw an error, but the program will exit at the line above, never reaching this point
            return desiredClass.cast(object);
        }
    }
    
    public static int toInt(TYObject tyObject) {
        
        if (tyObject instanceof TYLong) {
            
            return Math.toIntExact(((TYLong) tyObject).getInternalLong());
            
        } else if (tyObject instanceof TYFloat) {
            
            return (int) ((TYFloat) tyObject).getInternalDouble();
            
        } else if (tyObject instanceof TYString && ((TYString) tyObject).getInternalString().matches("[0-9]+")) {
            
            return Integer.parseInt(((TYString) tyObject).getInternalString());
            
        } else {
            
            return TrinityNatives.cast(TYInt.class, tyObject).getInternalInteger();
        }
    }
    
    public static long toLong(TYObject tyObject) {
        
        if (tyObject instanceof TYInt) {
            
            return ((TYInt) tyObject).getInternalInteger();
            
        } else if (tyObject instanceof TYFloat) {
            
            return (long) ((TYFloat) tyObject).getInternalDouble();
            
        } else if (tyObject instanceof TYString && ((TYString) tyObject).getInternalString().matches("[0-9]+")) {
            
            return Long.parseLong(((TYString) tyObject).getInternalString());
            
        } else {
            
            return TrinityNatives.cast(TYLong.class, tyObject).getInternalLong();
        }
    }
    
    public static double toFloat(TYObject tyObject) {
        
        if (tyObject instanceof TYInt) {
            
            return ((TYInt) tyObject).getInternalInteger();
            
        } else if (tyObject instanceof TYLong) {
            
            return ((TYLong) tyObject).getInternalLong();
            
        } else if (tyObject instanceof TYString && ((TYString) tyObject).getInternalString().matches("[0-9]*\\.?[0-9]+")) {
            
            return Double.parseDouble(((TYString) tyObject).getInternalString());
            
        } else {
            
            return TrinityNatives.cast(TYFloat.class, tyObject).getInternalDouble();
        }
    }
    
    public static String toString(TYObject tyObject, TYRuntime runtime) {
        
        if (tyObject instanceof TYString) {
            
            return ((TYString) tyObject).getInternalString();
            
        } else {
            
            TYString tyString = cast(TYString.class, tyObject.tyInvoke("toString", runtime, null, null));
            return tyString.getInternalString();
        }
    }
    
    public static boolean toBoolean(TYObject tyObject) {
        
        if (tyObject == TYObject.NIL || tyObject == TYObject.NONE) {
            
            return false;
            
        } else if (tyObject instanceof TYBoolean) {
            
            return ((TYBoolean) tyObject).getInternalBoolean();
            
        } else if (tyObject instanceof TYInt) {
            
            return ((TYInt) tyObject).getInternalInteger() != 0;
            
        } else if (tyObject instanceof TYLong) {
            
            return ((TYLong) tyObject).getInternalLong() != 0;
            
        } else if (tyObject instanceof TYFloat) {
            
            return ((TYFloat) tyObject).getInternalDouble() != 0;
            
        } else {
            
            return true;
        }
    }
    
    public static TYObject wrapNumber(double d) {
        
        if (d % 1 == 0) {
            
            if (d > Integer.MAX_VALUE || d < Integer.MIN_VALUE) {
                
                return new TYLong((long) d);
                
            } else {
                
                return new TYInt((int) d);
            }
            
        } else {
            
            return new TYFloat(d);
        }
    }
}
