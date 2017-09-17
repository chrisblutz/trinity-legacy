package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.TYFieldObject;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;


/**
 * @author Christopher Lutz
 */
class NativeField {
    
    protected static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.FIELD, "getName", (runtime, thisObj, params) -> NativeStorage.getFieldName(TrinityNatives.cast(TYFieldObject.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.FIELD, "isStatic", (runtime, thisObj, params) -> NativeStorage.isFieldStatic(TrinityNatives.cast(TYFieldObject.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.FIELD, "isNative", (runtime, thisObj, params) -> NativeStorage.isFieldNative(TrinityNatives.cast(TYFieldObject.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.FIELD, "isConstant", (runtime, thisObj, params) -> NativeStorage.isFieldConstant(TrinityNatives.cast(TYFieldObject.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.FIELD, "getComments", (runtime, thisObj, params) -> NativeStorage.getLeadingComments(TrinityNatives.cast(TYFieldObject.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.FIELD, "getValue", (runtime, thisObj, params) -> {
            
            TYFieldObject fieldObject = TrinityNatives.cast(TYFieldObject.class, thisObj);
            TYObject activeObj = runtime.getVariable("obj");
            
            VariableLoc loc;
            if (fieldObject.isStatic()) {
                
                loc = fieldObject.getInternalClass().getVariable(fieldObject.getInternalName());
                
            } else {
                
                loc = fieldObject.getInternalClass().getVariable(fieldObject.getInternalName(), activeObj);
            }
            
            if (loc.checkScope(runtime)) {
                
                return loc.getValue();
                
            } else {
                
                loc.getScope().reportAccessViolation(runtime);
            }
            
            return TYObject.NIL;
        });
    }
}
