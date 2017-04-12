package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInheritanceError;


/**
 * @author Christopher Lutz
 */
public class TYObject {
    
    public static final TYObject NIL = new TYObject(ClassRegistry.getClass("Nil")), NONE = new TYObject(ClassRegistry.getClass("Class"));
    
    private TYClass objClass;
    private int superStack = 0;
    
    public TYObject(TYClass objClass) {
        
        this.objClass = objClass;
    }
    
    public TYClass getObjectClass() {
        
        return objClass;
    }
    
    public void superify() {
        
        superStack++;
    }
    
    public int getSuperStackLevel() {
        
        return superStack;
    }
    
    public TYObject tyInvoke(String methodName, TYRuntime runtime, TYStackTrace stackTrace, TYObject... params) {
        
        if (superStack == 0) {
            
            return getObjectClass().tyInvoke(methodName, runtime, stackTrace, this, params);
            
        } else {
            
            TYClass superClass = getObjectClass();
            for (int i = 0; i < superStack; i++) {
                
                superClass = superClass.getSuperclass();
                
                if (superClass == null) {
                    
                    TYError error = new TYError(new TYInheritanceError(), "Superclass does not exist.", stackTrace);
                    error.throwError();
                    break;
                }
            }
            
            TYObject obj = NIL;
            
            if (superClass != null) {
                
                obj = superClass.tyInvoke(methodName, runtime, stackTrace, this, params);
            }
            
            superStack--;
            
            return obj;
        }
    }
}
