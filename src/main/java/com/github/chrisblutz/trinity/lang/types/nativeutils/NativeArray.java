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
    
    protected static void register() {
        
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "length", (runtime, thisObj, params) -> NativeStorage.getArrayLength(TrinityNatives.cast(TYArray.class, thisObj)));
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "add", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return TYBoolean.valueFor(thisArray.getInternalList().add(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "insert", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            thisArray.getInternalList().add(TrinityNatives.toInt(runtime.getVariable("index")), runtime.getVariable("value"));
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "remove", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return thisArray.getInternalList().remove(TrinityNatives.toInt(runtime.getVariable("index")));
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "clear", (runtime, thisObj, params) -> {
            
            TrinityNatives.cast(TYArray.class, thisObj).getInternalList().clear();
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "[]", (runtime, thisObj, params) -> {
            
            int index = TrinityNatives.toInt(runtime.getVariable("index"));
            List<TYObject> thisList = TrinityNatives.cast(TYArray.class, thisObj).getInternalList();
            
            if (index >= thisList.size() || index < 0) {
                
                Errors.throwError(Errors.Classes.INDEX_OUT_OF_BOUNDS_ERROR, runtime, "Index: " + index + ", Size: " + thisList.size());
            }
            
            return thisList.get(index);
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "[]=", (runtime, thisObj, params) -> {
            
            int index = TrinityNatives.toInt(runtime.getVariable("index"));
            TYObject value = runtime.getVariable("value");
            List<TYObject> thisList = TrinityNatives.cast(TYArray.class, thisObj).getInternalList();
            
            if (index >= thisList.size() || index < 0) {
                
                Errors.throwError(Errors.Classes.INDEX_OUT_OF_BOUNDS_ERROR, runtime, "Index: " + index + ", Size: " + thisList.size());
            }
            
            thisList.set(index, value);
            
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod(TrinityNatives.Classes.ARRAY, "copyOf", (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, runtime.getVariable("array"));
            return new TYArray(new ArrayList<>(thisArray.getInternalList()));
        });
    }
}
