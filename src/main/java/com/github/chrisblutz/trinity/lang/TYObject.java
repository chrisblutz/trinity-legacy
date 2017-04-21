package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYNilClass;


/**
 * @author Christopher Lutz
 */
public class TYObject {
    
    public static final TYObject NIL = new TYObject(new TYNilClass()), NONE = new TYObject(ClassRegistry.getClass("Class"));
    
    private TYClass objClass;
    private int superStack = 0;
    
    public TYObject(TYClass objClass) {
        
        this.objClass = objClass;
    }
    
    public TYClass getObjectClass() {
        
        return objClass;
    }
    
    public void incrementStackLevel() {
        
        superStack++;
    }
    
    public int getSuperStackLevel() {
        
        return superStack;
    }
    
    public TYObject tyInvoke(String methodName, TYRuntime runtime, TYStackTrace stackTrace, TYProcedure procedure, TYRuntime procedureRuntime, TYObject... params) {
        
        if (superStack == 0) {
            
            return getObjectClass().tyInvoke(methodName, runtime, stackTrace, procedure, procedureRuntime, this, params);
            
        } else {
            
            TYClass superClass = getObjectClass();
            for (int i = 0; i < superStack; i++) {
                
                superClass = superClass.getSuperclass();
                
                if (superClass == null) {
                    
                    TYError error = new TYError("Trinity.Errors.InheritanceError", "Superclass does not exist.", stackTrace);
                    error.throwError();
                    break;
                }
            }
            
            TYObject obj = NIL;
            
            if (superClass != null) {
                
                obj = superClass.tyInvoke(methodName, runtime, stackTrace, procedure, procedureRuntime, this, params);
            }
            
            superStack--;
            
            return obj;
        }
    }
}
