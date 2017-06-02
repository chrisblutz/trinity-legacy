package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.interpreter.variables.Variables;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeNatives {
    
    static void register() {
        
        TrinityNatives.registerMethod("Natives", "initGlobalVariable", true, new String[]{"name"}, null, null, (runtime, thisObj, params) -> {
            
            String globalName = TrinityNatives.toString(runtime.getVariable("name"), runtime);
            
            ProcedureAction action = TrinityNatives.getGlobalProcedureAction(globalName);
            TYObject result;
            
            if (action != null) {
                
                result = action.onAction(runtime, TYObject.NONE);
                
            } else {
                
                result = TYObject.NIL;
            }
            
            Variables.setGlobalVariable(globalName, result);
            
            return TYObject.NONE;
        });
    }
}
