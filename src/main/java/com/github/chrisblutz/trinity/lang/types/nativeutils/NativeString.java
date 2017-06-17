package com.github.chrisblutz.trinity.lang.types.nativeutils;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
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
        
        TrinityNatives.registerMethod("Trinity.String", "chars", false, null, null, null, null, (runtime, thisObj, params) -> TrinityNatives.cast(TYString.class, thisObj).getCharacterArray());
        TrinityNatives.registerMethod("Trinity.String", "+", false, new String[]{"other"}, null, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.cast(TYString.class, thisObj).getInternalString();
            
            TYObject object = runtime.getVariable("other");
            String objStr = TrinityNatives.toString(object, runtime);
            
            return new TYString(thisString + objStr);
        });
        TrinityNatives.registerMethod("Trinity.String", "==", false, new String[]{"other"}, null, null, null, (runtime, thisObj, params) -> {
            
            TYObject object = runtime.getVariable("other");
            
            if (!(object instanceof TYString)) {
                
                return TYBoolean.FALSE;
            }
            
            return TYBoolean.valueFor(TrinityNatives.cast(TYString.class, thisObj).getInternalString().contentEquals(TrinityNatives.cast(TYString.class, object).getInternalString()));
        });
        Map<String, ProcedureAction> optionalParams = new HashMap<>();
        optionalParams.put("options", (runtime, thisObj, params1) -> TrinityNatives.getObjectFor(""));
        TrinityNatives.registerMethod("Trinity.String", "match", false, new String[]{"regex"}, optionalParams, null, null, (runtime, thisObj, params) -> {
            
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
            
            TYObject bool = TrinityNatives.getObjectFor(matcher.matches());
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
        optionalParams = new HashMap<>();
        optionalParams.put("options", (runtime, thisObj, params1) -> TrinityNatives.getObjectFor(""));
        TrinityNatives.registerMethod("Trinity.String", "matches", false, new String[]{"regex"}, optionalParams, null, null, (runtime, thisObj, params) -> {
            
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
            
            return TrinityNatives.getObjectFor(matcher.matches());
        });
        TrinityNatives.registerMethod("Trinity.String", "toUpperCase", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            return new TYString(thisString.toUpperCase());
        });
        TrinityNatives.registerMethod("Trinity.String", "toLowerCase", false, null, null, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            return new TYString(thisString.toLowerCase());
        });
        TrinityNatives.registerMethod("Trinity.String", "startsWith", false, new String[]{"prefix"}, null, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            String prefix = TrinityNatives.toString(runtime.getVariable("prefix"), runtime);
            return TYBoolean.valueFor(thisString.startsWith(prefix));
        });
        TrinityNatives.registerMethod("Trinity.String", "endsWith", false, new String[]{"suffix"}, null, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            String suffix = TrinityNatives.toString(runtime.getVariable("suffix"), runtime);
            return TYBoolean.valueFor(thisString.endsWith(suffix));
        });
        TrinityNatives.registerMethod("Trinity.String", "contains", false, new String[]{"str"}, null, null, null, (runtime, thisObj, params) -> {
            
            String thisString = TrinityNatives.toString(thisObj, runtime);
            String str = TrinityNatives.toString(runtime.getVariable("str"), runtime);
            return TYBoolean.valueFor(thisString.contains(str));
        });
        TrinityNatives.registerMethod("Trinity.String", "toInt", false, null, null, null, null, (runtime, thisObj, params) -> new TYInt(TrinityNatives.toInt(thisObj)));
        TrinityNatives.registerMethod("Trinity.String", "toLong", false, null, null, null, null, (runtime, thisObj, params) -> new TYLong(TrinityNatives.toLong(thisObj)));
        TrinityNatives.registerMethod("Trinity.String", "toFloat", false, null, null, null, null, (runtime, thisObj, params) -> new TYFloat(TrinityNatives.toFloat(thisObj)));
    }
}
