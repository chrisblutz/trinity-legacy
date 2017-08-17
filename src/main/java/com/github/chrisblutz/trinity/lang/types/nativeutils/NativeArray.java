package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeArray {
    
    static void register() {
        
        TrinityNatives.registerMethod("Trinity.Array", "length", (runtime, thisObj, params) -> NativeStorage.getArrayLength(TrinityNatives.cast(TYArray.class, thisObj)));
        TrinityNatives.registerMethod("Trinity.Array", "add", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return TYBoolean.valueFor(thisArray.getInternalList().add(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Trinity.Array", "insert", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            thisArray.getInternalList().add(TrinityNatives.toInt(runtime.getVariable("index")), runtime.getVariable("value"));
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Array", "remove", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return thisArray.getInternalList().remove(TrinityNatives.toInt(runtime.getVariable("index")));
        });
        TrinityNatives.registerMethod("Trinity.Array", "clear", (runtime, thisObj, params) -> {
            
            TrinityNatives.cast(TYArray.class, thisObj).getInternalList().clear();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Array", "[]", (runtime, thisObj, params) -> {
            
            int index = TrinityNatives.toInt(runtime.getVariable("index"));
            List<TYObject> thisList = TrinityNatives.cast(TYArray.class, thisObj).getInternalList();
            
            if (index >= thisList.size() || index < 0) {
                
                Errors.throwError("Trinity.Errors.IndexOutOfBoundsError", runtime, "Index: " + index + ", Size: " + thisList.size());
            }
            
            return thisList.get(index);
        });
        TrinityNatives.registerMethod("Trinity.Array", "[]=", (runtime, thisObj, params) -> {
            
            int index = TrinityNatives.toInt(runtime.getVariable("index"));
            TYObject value = runtime.getVariable("value");
            List<TYObject> thisList = TrinityNatives.cast(TYArray.class, thisObj).getInternalList();
            
            if (index >= thisList.size() || index < 0) {
                
                Errors.throwError("Trinity.Errors.IndexOutOfBoundsError", runtime, "Index: " + index + ", Size: " + thisList.size());
            }
            
            thisList.set(index, value);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Trinity.Array", "copyOf", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, runtime.getVariable("array"));
            return new TYArray(new ArrayList<>(thisArray.getInternalList()));
        });
    }
}
