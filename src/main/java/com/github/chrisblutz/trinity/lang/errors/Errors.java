package com.github.chrisblutz.trinity.lang.errors;

import com.github.chrisblutz.trinity.Trinity;
import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.interpreter.errors.TrinityErrorException;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.StackElement;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class Errors {
    
    public final class Classes {
        
        public static final String ARITHMETIC_ERROR = "Trinity.Errors.ArithmeticError";
        public static final String ASSERTION_ERROR = "Trinity.Errors.AssertionError";
        public static final String ASSIGNMENT_ERROR = "Trinity.Errors.AssignmentError";
        public static final String CAST_ERROR = "Trinity.Errors.CastError";
        public static final String CLASS_NOT_FOUND_ERROR = "Trinity.Errors.ClassNotFoundError";
        public static final String COMPLEX_NUMBER_ERROR = "Trinity.Errors.ComplexNumberError";
        public static final String ERROR = TrinityNatives.Classes.ERROR;
        public static final String FIELD_NOT_FOUND_ERROR = "Trinity.Errors.FieldNotFoundError";
        public static final String INDEX_OUT_OF_BOUNDS_ERROR = "Trinity.Errors.IndexOutOfBoundsError";
        public static final String INHERITANCE_ERROR = "Trinity.Errors.InheritanceError";
        public static final String INVALID_ARGUMENT_ERROR = "Trinity.Errors.InvalidArgumentError";
        public static final String INVALID_ARGUMENT_NUMBER_ERROR = "Trinity.Errors.InvalidArgumentNumberError";
        public static final String INVALID_TYPE_ERROR = "Trinity.Errors.InvalidTypeError";
        public static final String IO_ERROR = "Trinity.Errors.IOError";
        public static final String METHOD_NOT_FOUND_ERROR = "Trinity.Errors.MethodNotFoundError";
        public static final String NATIVE_TYPE_ERROR = "Trinity.Errors.NativeTypeError";
        public static final String NIL_ERROR = "Trinity.Errors.NilError";
        public static final String NUMBER_FORMAT_ERROR = "Trinity.Errors.NumberFormatError";
        public static final String RETURN_ERROR = "Trinity.Errors.ReturnError";
        public static final String RUNTIME_ERROR = "Trinity.Errors.RuntimeError";
        public static final String SCOPE_ERROR = "Trinity.Errors.ScopeError";
        public static final String STACK_OVERFLOW_ERROR = "Trinity.Errors.StackOverflowError";
        public static final String SYNTAX_ERROR = "Trinity.Errors.SyntaxError";
        public static final String UNSUPPORTED_OPERATION_ERROR = "Trinity.Errors.UnsupportedOperationError";
    }
    
    public static void throwError(String errorClass, Object... args) {
        
        throwError(errorClass, new TYRuntime(), args);
    }
    
    public static void throwError(String errorClass, TYRuntime runtime, Object... args) {
        
        TYObject error = constructError(errorClass, runtime, args);
        TrinityNatives.call(TrinityNatives.Classes.KERNEL, "throw", runtime, TYObject.NONE, error);
    }
    
    public static void throwSyntaxError(String errorClass, String message, String filename, int line) {
        
        throwSyntaxErrorDelayExit(errorClass, message, filename, line);
        
        exit();
    }
    
    public static void throwSyntaxErrorDelayExit(String errorClass, String message, String filename, int line) {
        
        // Mimic toString() method of Error class
        String str = errorClass;
        
        if (message != null && !message.isEmpty()) {
            
            str += ": " + message;
        }
        
        if (filename != null && line > 0) {
            
            str += "\n\tin '" + filename + "' at line " + line;
        }
        
        System.err.println(str);
    }
    
    public static void exit() {
        
        Trinity.exit(1);
    }
    
    public static void throwUnrecoverable(String errorClass, Object... args) {
        
        TYObject error = constructError(errorClass, new TYRuntime(), args);
        throwUncaughtJavaException(new TrinityErrorException(error), null, 0, TYThread.getCurrentThread());
    }
    
    private static TYObject constructError(String errorClass, TYRuntime runtime, Object... args) {
        
        TYObject[] tyArgs = new TYObject[args.length];
        for (int i = 0; i < args.length; i++) {
            
            Object o = args[i];
            
            if (o instanceof TYObject) {
                
                tyArgs[i] = (TYObject) o;
                
            } else {
                
                tyArgs[i] = TrinityNatives.getObjectFor(o);
            }
        }
        
        return TrinityNatives.newInstance(errorClass, runtime, tyArgs);
    }
    
    public static void throwUncaughtJavaException(Throwable error, String file, int line, TYThread thread) {
        
        if (error instanceof TrinityErrorException) {
            
            TYObject tyError = ((TrinityErrorException) error).getErrorObject();
            String errorMessage = TrinityNatives.cast(TYString.class, tyError.tyInvoke("toString", new TYRuntime(), null, null)).getInternalString();
            
            System.err.println(errorMessage);
            
        } else if (error instanceof StackOverflowError) {
            
            throwUnrecoverable(Classes.STACK_OVERFLOW_ERROR);
            
        } else {
            
            System.err.println("An error occurred in the Trinity interpreter in file '" + file + "' at line " + line + " on thread '" + thread.getName() + "'.");
            
            if (CLI.isDebuggingEnabled()) {
                
                System.err.println("\n== FULL ERROR ==\n");
                error.printStackTrace();
                
                System.err.println("\n== FULL TRINITY STACK ==\n");
                for (StackElement element : thread.getTrinityStack().getStack()) {
                    
                    System.err.println(element);
                }
                
            } else {
                
                System.err.println("To view a full trace, enable debugging with the -d/--debug option.");
            }
        }
    }
}
