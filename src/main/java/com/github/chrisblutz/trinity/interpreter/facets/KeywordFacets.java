package com.github.chrisblutz.trinity.interpreter.facets;

import com.github.chrisblutz.trinity.interpreter.Keywords;
import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.ModuleRegistry;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticModuleObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class KeywordFacets {
    
    public static void registerFacets() {
        
        Keywords.register(Token.LITERAL_STRING, (thisObj, info, location, runtime) -> new TYString(info.getContents()));
        Keywords.register(Token.NUMERIC_STRING, (thisObj, info, location, runtime) -> {
            
            String numString = info.getContents();
            
            if (numString.matches("[0-9]+[lL]")) {
                
                return new TYLong(Long.parseLong(numString.substring(0, numString.length() - 1)));
                
            } else if (numString.matches("[0-9]*\\.[0-9]+")) {
                
                return new TYFloat(Double.parseDouble(numString));
                
            } else if (numString.matches("[0-9]*\\.?[0-9]+[fF]")) {
                
                return new TYFloat(Double.parseDouble(numString.substring(0, numString.length() - 1)));
                
            } else if (numString.matches("[0-9]+")) {
                
                try {
                    
                    return new TYInt(Integer.parseInt(numString));
                    
                } catch (Exception e) {
                    
                    return new TYLong(Long.parseLong(numString));
                }
                
            } else {
                
                Errors.throwError("Trinity.Errors.NumberFormatError", runtime, "Invalid numeric string.");
                return TYObject.NONE;
            }
        });
        
        Keywords.register(Token.NIL, (thisObj, info, location, runtime) -> TYObject.NIL);
        Keywords.register(Token.TRUE, (thisObj, info, location, runtime) -> TYBoolean.TRUE);
        Keywords.register(Token.FALSE, (thisObj, info, location, runtime) -> TYBoolean.FALSE);
        
        Keywords.register(Token.SUPER, (thisObj, info, location, runtime) -> {
            
            if (runtime.isStaticScope()) {
                
                TYClass thisClass = runtime.getTyClass();
                return NativeStorage.getStaticClassObject(thisClass.getSuperclass());
                
            } else {
                
                TYObject thisPointer = runtime.getThis();
                thisPointer.incrementStackLevel();
                return thisPointer;
            }
        });
        Keywords.register(Token.THIS, (thisObj, info, location, runtime) -> {
            
            if (!runtime.isStaticScope()) {
                
                return runtime.getThis();
                
            } else {
                
                Errors.throwError("Trinity.Errors.ScopeError", runtime, "Cannot access 'this' in a static context.");
                return TYObject.NONE;
            }
        });
        
        Keywords.register(Token.__FILE__, (thisObj, info, location, runtime) -> new TYString(location.getFile().getAbsolutePath()));
        Keywords.register(Token.__LINE__, (thisObj, info, location, runtime) -> new TYInt(location.getLineNumber()));
        
        Keywords.register(Token.BLOCK_CHECK, (thisObj, info, location, runtime) -> TYBoolean.valueFor(runtime.getProcedure() != null));
        
        Keywords.register(Token.BREAK, (thisObj, info, location, runtime) -> {
            
            runtime.setBroken(true);
            return TYObject.NONE;
        });
        
        Keywords.register(Token.CLASS, (thisObj, info, location, runtime) -> {
            
            if (thisObj instanceof TYStaticClassObject) {
                
                TYClass tyClass = ((TYStaticClassObject) thisObj).getInternalClass();
                tyClass.runInitializationActions();
                
                return NativeStorage.getClassObject(tyClass);
                
            } else if (thisObj instanceof TYStaticModuleObject && ClassRegistry.classExists(((TYStaticModuleObject) thisObj).getInternalModule().getName())) {
                
                TYClass tyClass = ClassRegistry.getClass(((TYStaticModuleObject) thisObj).getInternalModule().getName());
                tyClass.runInitializationActions();
                
                return NativeStorage.getClassObject(tyClass);
                
            } else {
                
                Errors.throwError("Trinity.Errors.RuntimeError", runtime, "Cannot retrieve a class here.");
                return TYObject.NONE;
            }
        });
        Keywords.register(Token.MODULE, (thisObj, info, location, runtime) -> {
            
            if (thisObj instanceof TYStaticModuleObject) {
                
                return NativeStorage.getModuleObject(((TYStaticModuleObject) thisObj).getInternalModule());
                
            } else if (thisObj instanceof TYStaticClassObject && ModuleRegistry.moduleExists(((TYStaticClassObject) thisObj).getInternalClass().getName())) {
                
                return NativeStorage.getModuleObject(ModuleRegistry.getModule(((TYStaticClassObject) thisObj).getInternalClass().getName()));
                
            } else {
                
                Errors.throwError("Trinity.Errors.SyntaxError", runtime, "Cannot retrieve a module here.");
                return TYObject.NONE;
            }
        });
    }
}
