package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
class NativeArray {
    
    static void register() {
        
        TrinityNatives.registerMethod("Array", "toString", false, null, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            StringBuilder str = new StringBuilder("[");
            
            List<TYObject> objects = ((TYArray) thisObj).getInternalList();
            
            for (int i = 0; i < objects.size(); i++) {
                
                str.append(((TYString) objects.get(i).tyInvoke("toString", runtime, stackTrace, null, null)).getInternalString());
                
                if (i < objects.size() - 1) {
                    
                    str.append(", ");
                }
            }
            
            str.append("]");
            
            return new TYString(str.toString());
        });
        TrinityNatives.registerMethod("Array", "length", false, null, null, null, (runtime, stackTrace, thisObj, params) -> NativeStorage.getArrayLength((TYArray) thisObj));
        TrinityNatives.registerMethod("Array", "add", false, new String[]{"value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            NativeStorage.clearArrayData((TYArray) thisObj);
            
            return new TYBoolean(((TYArray) thisObj).getInternalList().add(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Array", "insert", false, new String[]{"index", "value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            NativeStorage.clearArrayData((TYArray) thisObj);
            
            ((TYArray) thisObj).getInternalList().add(((TYInt) runtime.getVariable("index")).getInternalInteger(), runtime.getVariable("value"));
            return TYObject.NONE;
        });
        TrinityNatives.registerMethod("Array", "remove", false, new String[]{"index"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            NativeStorage.clearArrayData((TYArray) thisObj);
            
            return ((TYArray) thisObj).getInternalList().remove(((TYInt) runtime.getVariable("index")).getInternalInteger());
        });
        TrinityNatives.registerMethod("Array", "removeObject", false, new String[]{"value"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            NativeStorage.clearArrayData((TYArray) thisObj);
            
            return new TYBoolean(((TYArray) thisObj).getInternalList().remove(runtime.getVariable("value")));
        });
        TrinityNatives.registerMethod("Array", "+", false, new String[]{"other"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYArray thisArray = (TYArray) thisObj;
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
        TrinityNatives.registerMethod("Array", "[]", false, new String[]{"index"}, null, null, (runtime, stackTrace, thisObj, params) -> {
            
            TYObject obj = runtime.getVariable("index");
            
            if (obj instanceof TYInt) {
                
                return ((TYArray) thisObj).getInternalList().get(((TYInt) obj).getInternalInteger());
                
            } else {
                
                TYError error = new TYError("Trinity.Errors.InvalidTypeError", "'[]' takes an Int parameter.", stackTrace);
                error.throwError();
            }
            
            return TYObject.NIL;
        });
    }
}
