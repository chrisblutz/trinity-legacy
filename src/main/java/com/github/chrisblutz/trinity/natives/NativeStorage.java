package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.types.*;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.*;


/**
 * This class regulates attributes of native classes.  For instance, a call
 * to {@code object.getClass()} in Trinity source code will always return the same
 * instance of the native Java class {@code TYClassObject}, instead of recreating
 * an instance every time the method is called.
 *
 * @author Christopher Lutz
 */
public class NativeStorage {
    
    private static Map<TYClass, TYClassObject> classObjects = new HashMap<>();
    private static Map<TYClass, TYStaticClassObject> staticClassObjects = new HashMap<>();
    private static Map<TYModule, TYModuleObject> moduleObjects = new HashMap<>();
    private static Map<TYModule, TYStaticModuleObject> staticModuleObjects = new HashMap<>();
    private static Map<TYMethod, TYMethodObject> methodObjects = new HashMap<>();
    
    private static Map<TYClass, TYString> classNames = new HashMap<>();
    private static Map<TYClass, TYString> classShortNames = new HashMap<>();
    
    private static Map<TYModule, TYString> moduleNames = new HashMap<>();
    private static Map<TYModule, TYString> moduleShortNames = new HashMap<>();
    
    private static Map<TYMethod, TYString> methodNames = new HashMap<>();
    private static Map<TYMethod, TYBoolean> methodStatic = new HashMap<>();
    private static Map<TYMethod, TYBoolean> methodNative = new HashMap<>();
    private static Map<TYMethod, TYBoolean> methodSecure = new HashMap<>();
    
    private static Map<TYProcedure, TYArray> mandatoryArguments = new WeakHashMap<>();
    private static Map<TYProcedure, TYArray> optionalArguments = new WeakHashMap<>();
    private static Map<TYProcedure, TYObject> blockArguments = new WeakHashMap<>();
    private static Map<TYProcedure, TYObject> overflowArguments = new WeakHashMap<>();
    
    private static Map<TYArray, TYInt> arrayLengths = new HashMap<>();
    
    private static Map<TYMap, TYArray> mapKeySets = new WeakHashMap<>();
    private static Map<TYMap, TYArray> mapValues = new WeakHashMap<>();
    private static Map<TYMap, TYInt> mapLengths = new WeakHashMap<>();
    
    private static Map<TYObject, TYInt> hashCodes = new WeakHashMap<>();
    
    private static Map<TYClass, TYObject> classLeadingComments = new HashMap<>();
    private static Map<TYModule, TYObject> moduleLeadingComments = new HashMap<>();
    private static Map<TYMethod, TYObject> methodLeadingComments = new HashMap<>();
    
    private static TYString nilString = null;
    private static TYFloat e = null, pi = null;
    
    public static TYClassObject getClassObject(TYClass tyClass) {
        
        if (!classObjects.containsKey(tyClass)) {
            
            classObjects.put(tyClass, new TYClassObject(tyClass));
        }
        
        return classObjects.get(tyClass);
    }
    
    public static TYStaticClassObject getStaticClassObject(TYClass tyClass) {
        
        if (!staticClassObjects.containsKey(tyClass)) {
            
            staticClassObjects.put(tyClass, new TYStaticClassObject(tyClass));
        }
        
        return staticClassObjects.get(tyClass);
    }
    
    public static TYModuleObject getModuleObject(TYModule tyModule) {
        
        if (!moduleObjects.containsKey(tyModule)) {
            
            moduleObjects.put(tyModule, new TYModuleObject(tyModule));
        }
        
        return moduleObjects.get(tyModule);
    }
    
    public static TYStaticModuleObject getStaticModuleObject(TYModule tyModule) {
        
        if (!staticModuleObjects.containsKey(tyModule)) {
            
            staticModuleObjects.put(tyModule, new TYStaticModuleObject(tyModule));
        }
        
        return staticModuleObjects.get(tyModule);
    }
    
    public static TYMethodObject getMethodObject(TYMethod tyMethod) {
        
        if (!methodObjects.containsKey(tyMethod)) {
            
            methodObjects.put(tyMethod, new TYMethodObject(tyMethod));
        }
        
        return methodObjects.get(tyMethod);
    }
    
    public static TYString getClassName(TYClass tyClass) {
        
        if (!classNames.containsKey(tyClass)) {
            
            classNames.put(tyClass, new TYString(tyClass.getName()));
        }
        
        return classNames.get(tyClass);
    }
    
    public static TYString getClassShortName(TYClass tyClass) {
        
        if (!classShortNames.containsKey(tyClass)) {
            
            classShortNames.put(tyClass, new TYString(tyClass.getName()));
        }
        
        return classShortNames.get(tyClass);
    }
    
    public static TYString getModuleName(TYModule tyModule) {
        
        if (!moduleNames.containsKey(tyModule)) {
            
            moduleNames.put(tyModule, new TYString(tyModule.getName()));
        }
        
        return moduleNames.get(tyModule);
    }
    
    public static TYString getModuleShortName(TYModule tyModule) {
        
        if (!moduleShortNames.containsKey(tyModule)) {
            
            moduleShortNames.put(tyModule, new TYString(tyModule.getName()));
        }
        
        return moduleShortNames.get(tyModule);
    }
    
    public static TYString getMethodName(TYMethod tyMethod) {
        
        if (!methodNames.containsKey(tyMethod)) {
            
            methodNames.put(tyMethod, new TYString(tyMethod.getName()));
        }
        
        return methodNames.get(tyMethod);
    }
    
    public static TYBoolean isMethodStatic(TYMethod tyMethod) {
        
        if (!methodStatic.containsKey(tyMethod)) {
            
            methodStatic.put(tyMethod, TYBoolean.valueFor(tyMethod.isStaticMethod()));
        }
        
        return methodStatic.get(tyMethod);
    }
    
    public static TYBoolean isMethodNative(TYMethod tyMethod) {
        
        if (!methodNative.containsKey(tyMethod)) {
            
            methodNative.put(tyMethod, TYBoolean.valueFor(tyMethod.isNativeMethod()));
        }
        
        return methodNative.get(tyMethod);
    }
    
    public static TYBoolean isMethodSecure(TYMethod tyMethod) {
        
        if (!methodSecure.containsKey(tyMethod)) {
            
            methodSecure.put(tyMethod, TYBoolean.valueFor(tyMethod.isSecureMethod()));
        }
        
        return methodSecure.get(tyMethod);
    }
    
    public static TYArray getMandatoryArguments(TYProcedure tyProcedure) {
        
        if (!mandatoryArguments.containsKey(tyProcedure)) {
            
            List<String> arguments = tyProcedure.getMandatoryParameters();
            
            mandatoryArguments.put(tyProcedure, TrinityNatives.getArrayFor(arguments.toArray(new String[arguments.size()])));
        }
        
        return mandatoryArguments.get(tyProcedure);
    }
    
    public static TYArray getOptionalArguments(TYProcedure tyProcedure) {
        
        if (!optionalArguments.containsKey(tyProcedure)) {
            
            Set<String> arguments = tyProcedure.getOptionalParameters().keySet();
            
            optionalArguments.put(tyProcedure, TrinityNatives.getArrayFor(arguments.toArray(new String[arguments.size()])));
        }
        
        return optionalArguments.get(tyProcedure);
    }
    
    public static TYObject getBlockArgument(TYProcedure tyProcedure) {
        
        if (!blockArguments.containsKey(tyProcedure)) {
            
            blockArguments.put(tyProcedure, tyProcedure.getBlockParameter() == null ? TYObject.NIL : new TYString(tyProcedure.getBlockParameter()));
        }
        
        return blockArguments.get(tyProcedure);
    }
    
    public static TYObject getOverflowArgument(TYProcedure tyProcedure) {
        
        if (!overflowArguments.containsKey(tyProcedure)) {
            
            overflowArguments.put(tyProcedure, tyProcedure.getOverflowParameter() == null ? TYObject.NIL : new TYString(tyProcedure.getOverflowParameter()));
        }
        
        return overflowArguments.get(tyProcedure);
    }
    
    public static TYInt getArrayLength(TYArray tyArray) {
        
        if (!arrayLengths.containsKey(tyArray)) {
            
            arrayLengths.put(tyArray, new TYInt(tyArray.size()));
        }
        
        return arrayLengths.get(tyArray);
    }
    
    public static void clearArrayData(TYArray tyArray) {
        
        arrayLengths.remove(tyArray);
    }
    
    public static TYArray getMapKeySet(TYMap tyMap) {
        
        if (!mapKeySets.containsKey(tyMap)) {
            
            Set<TYObject> keys = tyMap.getInternalMap().keySet();
            List<TYObject> keyList = new ArrayList<>(keys);
            mapKeySets.put(tyMap, new TYArray(keyList));
        }
        
        return mapKeySets.get(tyMap);
    }
    
    public static TYArray getMapValues(TYMap tyMap) {
        
        if (!mapValues.containsKey(tyMap)) {
            
            Collection<TYObject> keys = tyMap.getInternalMap().values();
            List<TYObject> keyList = new ArrayList<>(keys);
            mapValues.put(tyMap, new TYArray(keyList));
        }
        
        return mapValues.get(tyMap);
    }
    
    public static TYInt getMapLength(TYMap tyMap) {
        
        if (!mapLengths.containsKey(tyMap)) {
            
            mapLengths.put(tyMap, new TYInt(tyMap.size()));
        }
        
        return mapLengths.get(tyMap);
    }
    
    public static void clearMapData(TYMap tyMap) {
        
        mapKeySets.remove(tyMap);
        mapValues.remove(tyMap);
        mapLengths.remove(tyMap);
    }
    
    public static TYInt getHashCode(TYObject tyObject) {
        
        if (!hashCodes.containsKey(tyObject)) {
            
            hashCodes.put(tyObject, tyObject == TYObject.NIL ? new TYInt(0) : new TYInt(tyObject.hashCode()));
        }
        
        return hashCodes.get(tyObject);
    }
    
    public static TYString getNilString() {
        
        if (nilString == null) {
            
            nilString = new TYString("nil");
        }
        
        return nilString;
    }
    
    public static TYObject getLeadingComments(TYClass tyClass) {
        
        if (!classLeadingComments.containsKey(tyClass)) {
            
            String[] c = tyClass.getLeadingComments();
            classLeadingComments.put(tyClass, TrinityNatives.getObjectFor(c));
        }
        
        return classLeadingComments.get(tyClass);
    }
    
    public static TYObject getLeadingComments(TYModule tyModule) {
        
        if (!moduleLeadingComments.containsKey(tyModule)) {
            
            String[] c = tyModule.getLeadingComments();
            moduleLeadingComments.put(tyModule, TrinityNatives.getObjectFor(c));
        }
        
        return moduleLeadingComments.get(tyModule);
    }
    
    public static TYObject getLeadingComments(TYMethod tyMethod) {
        
        if (!methodLeadingComments.containsKey(tyMethod)) {
            
            String[] c = tyMethod.getLeadingComments();
            methodLeadingComments.put(tyMethod, TrinityNatives.getObjectFor(c));
        }
        
        return methodLeadingComments.get(tyMethod);
    }
    
    public static TYFloat getE() {
        
        if (e == null) {
            
            e = new TYFloat(Math.E);
        }
        
        return e;
    }
    
    public static TYFloat getPi() {
        
        if (pi == null) {
            
            pi = new TYFloat(Math.PI);
        }
        
        return pi;
    }
}
