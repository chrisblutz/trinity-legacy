package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.TYNilClass;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
public class TYObject {
    
    public static final TYObject NIL = new TYObject(new TYNilClass()), NONE = new TYObject(ClassRegistry.getClass(TrinityNatives.Classes.CLASS));
    
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
    
    public TYObject tyInvoke(String methodName, TYRuntime runtime, TYProcedure procedure, TYRuntime procedureRuntime, TYObject... params) {
        
        if (superStack == 0) {
            
            return getObjectClass().tyInvoke(methodName, runtime, procedure, procedureRuntime, this, params);
            
        } else {
            
            TYClass superClass = getObjectClass();
            for (int i = 0; i < superStack; i++) {
                
                superClass = superClass.getSuperclass();
                
                if (superClass == null) {
                    
                    Errors.throwError(Errors.Classes.INHERITANCE_ERROR, runtime, "Superclass does not exist.");
                    break;
                }
            }
            
            TYObject obj = NIL;
            
            if (superClass != null) {
                
                obj = superClass.tyInvoke(methodName, runtime, procedure, procedureRuntime, this, params);
            }
            
            superStack--;
            
            return obj;
        }
    }
}
