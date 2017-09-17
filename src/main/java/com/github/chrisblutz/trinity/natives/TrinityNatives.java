package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class houses the central API for native methods and fields in Trinity.
 * It also provides utility methods for object construction, method invocation, and native
 * type conversion.
 *
 * @author Christopher Lutz
 */
public class TrinityNatives {
    
    public class Classes {
        
        public static final String ARRAY = "Trinity.Array";
        public static final String BOOLEAN = "Trinity.Boolean";
        public static final String CLASS = "Trinity.Class";
        public static final String COMPLEX_NUMBER = "Trinity.Math.ComplexNumber";
        public static final String ERROR = "Trinity.Errors.Error";
        public static final String FIELD = "Trinity.Field";
        public static final String FILE_SYSTEM = "Trinity.IO.Files.FileSystem";
        public static final String FLOAT = "Trinity.Float";
        public static final String INT = "Trinity.Int";
        public static final String KERNEL = "Trinity.Kernel";
        public static final String LONG = "Trinity.Long";
        public static final String MAP = "Trinity.Map";
        public static final String MATH = "Trinity.Math";
        public static final String METHOD = "Trinity.Method";
        public static final String MODULE = "Trinity.Module";
        public static final String OBJECT = "Trinity.Object";
        public static final String NATIVE_OUTPUT_STREAM = "Trinity.IO.NativeOutputStream";
        public static final String PROCEDURE = "Trinity.Procedure";
        public static final String STRING = "Trinity.String";
        public static final String SYSTEM = "Trinity.System";
        public static final String THREAD = "Trinity.Thread";
    }
    
    private static Map<String, Map<String, ProcedureAction>> methods = new HashMap<>();
    private static Map<String, Map<String, ProcedureAction>> fields = new HashMap<>();
    
    private static List<TYClass> nativeConstructors = new ArrayList<>();
    
    public static void registerMethod(String className, String methodName, ProcedureAction action) {
        
        ProcedureAction actionWithStackTrace = (runtime, thisObj, params) -> {
            
            TYThread current = TYThread.getCurrentThread();
            
            current.getTrinityStack().add(className, methodName, null, 0);
            
            TYObject result = action.onAction(runtime, thisObj, params);
            
            current.getTrinityStack().pop();
            
            return result;
        };
        
        if (!methods.containsKey(className)) {
            
            methods.put(className, new HashMap<>());
        }
        
        methods.get(className).put(methodName, actionWithStackTrace);
    }
    
    public static ProcedureAction getMethodProcedureAction(String className, String methodName, String fileName, int lineNumber) {
        
        if (methods.containsKey(className) && methods.get(className).containsKey(methodName)) {
            
            return methods.get(className).get(methodName);
            
        } else {
            
            Errors.throwSyntaxError(Errors.Classes.NATIVE_TYPE_ERROR, "Native method " + className + "." + methodName + " not implemented.", fileName, lineNumber);
            return null;
        }
    }
    
    public static void registerField(String className, String varName, ProcedureAction action) {
        
        if (!fields.containsKey(className)) {
            
            fields.put(className, new HashMap<>());
        }
        
        fields.get(className).put(varName, action);
    }
    
    public static ProcedureAction getFieldProcedureAction(String className, String varName, String fileName, int lineNumber) {
        
        if (fields.containsKey(className) && fields.get(className).containsKey(varName)) {
            
            return fields.get(className).get(varName);
            
        } else {
            
            Errors.throwSyntaxError(Errors.Classes.NATIVE_TYPE_ERROR, "Native field " + className + "." + varName + " not implemented.", fileName, lineNumber);
            return null;
        }
    }
    
    public static void registerForNativeConstruction(String className) {
        
        nativeConstructors.add(ClassRegistry.getClass(className));
    }
    
    public static boolean isClassNativelyConstructed(TYClass tyClass) {
        
        return nativeConstructors.contains(tyClass);
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
        
        Errors.throwSyntaxError(Errors.Classes.NATIVE_TYPE_ERROR, "Trinity does not have native type-conversion utilities for " + obj.getClass() + ".", null, 0);
        
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
            
            Errors.throwError(Errors.Classes.CLASS_NOT_FOUND_ERROR, runtime, "Class " + className + " does not exist.");
        }
        
        return TYObject.NIL;
    }
    
    public static <T extends TYObject> T cast(Class<T> desiredClass, TYObject object) {
        
        if (desiredClass.isInstance(object)) {
            
            return desiredClass.cast(object);
            
        } else {
            
            Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, "Unexpected value of type " + object.getObjectClass().getName() + " found.");
            
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
            
            if (isInstance(tyObject, Classes.COMPLEX_NUMBER)) {
                
                if (getImaginaryComplexComponent(tyObject) == 0) {
                    
                    return (int) getRealComplexComponent(tyObject);
                    
                } else {
                    
                    TYRuntime runtime = new TYRuntime();
                    String str = toString(tyObject, runtime);
                    Errors.throwError(Errors.Classes.COMPLEX_NUMBER_ERROR, "Real number required. Found: " + str);
                }
            }
            
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
            
            if (isInstance(tyObject, Classes.COMPLEX_NUMBER)) {
                
                if (getImaginaryComplexComponent(tyObject) == 0) {
                    
                    return (long) getRealComplexComponent(tyObject);
                    
                } else {
                    
                    TYRuntime runtime = new TYRuntime();
                    String str = toString(tyObject, runtime);
                    Errors.throwError(Errors.Classes.COMPLEX_NUMBER_ERROR, "Real number required. Found: " + str);
                }
            }
            
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
            
            if (isInstance(tyObject, Classes.COMPLEX_NUMBER)) {
                
                if (getImaginaryComplexComponent(tyObject) == 0) {
                    
                    return getRealComplexComponent(tyObject);
                    
                } else {
                    
                    TYRuntime runtime = new TYRuntime();
                    String str = toString(tyObject, runtime);
                    Errors.throwError(Errors.Classes.COMPLEX_NUMBER_ERROR, "Real number required. Found: " + str);
                }
            }
            
            return TrinityNatives.cast(TYFloat.class, tyObject).getInternalDouble();
        }
    }
    
    private static double getImaginaryComplexComponent(TYObject object) {
        
        return toFloat(object.tyInvoke("imaginary", new TYRuntime(), null, null));
    }
    
    private static double getRealComplexComponent(TYObject object) {
        
        return toFloat(object.tyInvoke("real", new TYRuntime(), null, null));
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
    
    /**
     * Wraps a Java number inside of Trinity's number types.  This method
     * only accepts a double, but will return any of Trinity's types
     * ({@code Int}, {@code Long}, and {@code Float}).  If the value is
     * an integer, this method will return an {@code Int}.  If that integer
     * overflows Java's primitive {@code int} type, this method will return
     * a {@code Long}.  If the value is a decimal, this method will return
     * a {@code Float}.
     *
     * @param d The number to wrap
     * @return The wrapped object, contained within a {@code TYObject} instance
     */
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
    
    public static TYObject wrapComplexNumber(double real, double imaginary) {
        
        return ClassRegistry.getClass(Classes.KERNEL).tyInvoke("cmplx", new TYRuntime(), null, null, null, wrapNumber(real), wrapNumber(imaginary));
    }
    
    /**
     * Checks if an object is an instance of a Trinity class or any subclasses
     * of that class.
     *
     * @param object    The object to check
     * @param className The inherited class
     * @return Whether or not the object is an instance of a class
     */
    public static boolean isInstance(TYObject object, String className) {
        
        return object.getObjectClass().isInstanceOf(ClassRegistry.getClass(className));
    }
}
