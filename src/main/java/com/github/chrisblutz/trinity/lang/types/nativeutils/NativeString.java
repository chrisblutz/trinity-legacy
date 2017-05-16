package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Christopher Lutz
 */
class NativeString {
    
    static void register() {
        
        TrinityNatives.registerMethod("String", "chars", false, null, null, null, (runtime, thisObj, params) -> TrinityNatives.cast(TYString.class, thisObj).getCharacterArray());
        TrinityNatives.registerMethod("String", "+", false, new String[]{"other"}, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            
            TYObject object = runtime.getVariable("other");
            String objStr = TrinityNatives.cast(TYString.class, object.tyInvoke("toString", runtime, null, null)).getInternalString();
            
            return new TYString(thisString + objStr);
        });
        TrinityNatives.registerMethod("String", "==", false, new String[]{"other"}, null, null, (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (!(object instanceof TYString)) {
                
                return TYBoolean.FALSE;
            }
            
            return TYBoolean.valueFor(TrinityNatives.cast(TYString.class, thisObj).getInternalString().contentEquals(TrinityNatives.cast(TYString.class, object).getInternalString()));
        });
        Map<String, TYObject> params = new HashMap<>();
        params.put("options", TrinityNatives.getObjectFor(""));
        TrinityNatives.registerMethod("String", "match", false, new String[]{"regex"}, params, null, (runtime, thisObj, params12) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            String regex = TrinityNatives.cast(TYString.class, runtime.getVariable("regex")).getInternalString();
            String options = TrinityNatives.cast(TYString.class, runtime.getVariable("options")).getInternalString();
            
            int flags = 0;
            if (options.contains("i")) {
                
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (options.contains("m")) {
                
                flags |= Pattern.MULTILINE;
            }
            if (options.contains("x")) {
                
                flags |= Pattern.COMMENTS;
            }
            if (options.contains("d")) {
                
                flags |= Pattern.DOTALL;
            }
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(thisString);
            
            TYBoolean bool = TrinityNatives.cast(TYBoolean.class, TrinityNatives.getObjectFor(matcher.matches()));
            TYArray array;
            if (matcher.matches()) {
                
                String[] groups = new String[matcher.groupCount() + 1];
                for (int i = 0; i < matcher.groupCount() + 1; i++) {
                    
                    groups[i] = matcher.group(i);
                }
                array = TrinityNatives.getArrayFor(groups);
                
            } else {
                
                array = new TYArray(new ArrayList<>());
            }
            
            return TrinityNatives.newInstance("Trinity.StringUtils.Regex", runtime, bool, array);
        });
        params = new HashMap<>();
        params.put("options", TrinityNatives.getObjectFor(""));
        TrinityNatives.registerMethod("String", "matches", false, new String[]{"regex"}, params, null, (runtime, thisObj, params1) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            String regex = TrinityNatives.cast(TYString.class, runtime.getVariable("regex")).getInternalString();
            String options = TrinityNatives.cast(TYString.class, runtime.getVariable("options")).getInternalString();
            
            int flags = 0;
            if (options.contains("i")) {
                
                flags |= Pattern.CASE_INSENSITIVE;
            }
            if (options.contains("m")) {
                
                flags |= Pattern.MULTILINE;
            }
            if (options.contains("x")) {
                
                flags |= Pattern.COMMENTS;
            }
            if (options.contains("d")) {
                
                flags |= Pattern.DOTALL;
            }
            
            Pattern pattern = Pattern.compile(regex, flags);
            Matcher matcher = pattern.matcher(thisString);
            
            return TrinityNatives.cast(TYBoolean.class, TrinityNatives.getObjectFor(matcher.matches()));
        });
    }
}
