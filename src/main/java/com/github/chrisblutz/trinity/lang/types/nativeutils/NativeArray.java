package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeArray {
    
    static void register() {
        
        TrinityNatives.registerMethod("Array", "length", false, null, null, null, (runtime, thisObj, params) -> NativeStorage.getArrayLength(TrinityNatives.cast(TYArray.class, thisObj)));
        TrinityNatives.registerMethod("Array", "add", false, new String[]{"value"}, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return TYBoolean.valueFor(thisArray.getInternalList().add(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Array", "insert", false, new String[]{"index", "value"}, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            thisArray.getInternalList().add(TrinityNatives.cast(TYInt.class, runtime.getVariable("index")).getInternalInteger(), runtime.getVariable("value"));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Array", "remove", false, new String[]{"index"}, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return thisArray.getInternalList().remove(TrinityNatives.cast(TYInt.class, runtime.getVariable("index")).getInternalInteger());
        });
        TrinityNatives.registerMethod("Array", "removeObject", false, new String[]{"value"}, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            
            NativeStorage.clearArrayData(thisArray);
            
            return TYBoolean.valueFor(thisArray.getInternalList().remove(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Array", "+", false, new String[]{"other"}, null, null, (runtime, thisObj, params) -> {
            
            TYArray thisArray = TrinityNatives.cast(TYArray.class, thisObj);
            List<TYObject> objects = new ArrayList<>();
            objects.addAll(thisArray.getInternalList());
            
            TYObject obj = runtime.getVariable("other");
            
            if (obj instanceof TYArray) {
                
                objects.addAll(((TYArray) obj).getInternalList());
                
            } else {
                
                objects.add(obj);
            }
            
            return new TYArray(objects);
        });
        TrinityNatives.registerMethod("Array", "[]", false, new String[]{"index"}, null, null, (runtime, thisObj, params) -> {
            
            int index = TrinityNatives.cast(TYInt.class, runtime.getVariable("index")).getInternalInteger();
            List<TYObject> thisList = TrinityNatives.cast(TYArray.class, thisObj).getInternalList();
            
            if (index >= thisList.size() || index < 0) {
                
                TYError error = new TYError("Trinity.Errors.IndexOutOfBoundsError", "Index: " + index + ", Size: " + thisList.size());
                error.throwError();
            }
            
            return thisList.get(index);
        });
    }
}
