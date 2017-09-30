package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.threading.TYThread;
import com.github.chrisblutz.trinity.lang.types.*;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.lang.types.threading.TYThreadObject;

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
    private static Map<TYClass, Map<String, TYFieldObject>> fieldObjects = new HashMap<>();
    
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
    
    private static Map<TYFieldObject, TYString> fieldNames = new HashMap<>();
    private static Map<TYFieldObject, TYBoolean> fieldStatic = new HashMap<>();
    private static Map<TYFieldObject, TYBoolean> fieldNative = new HashMap<>();
    private static Map<TYFieldObject, TYBoolean> fieldConstant = new HashMap<>();
    
    private static Map<TYArray, TYInt> arrayLengths = new HashMap<>();
    
    private static Map<TYMap, TYArray> mapKeySets = new WeakHashMap<>();
    private static Map<TYMap, TYArray> mapValues = new WeakHashMap<>();
    private static Map<TYMap, TYInt> mapLengths = new WeakHashMap<>();
    
    private static Map<TYObject, TYInt> hashCodes = new WeakHashMap<>();
    
    private static Map<TYClass, TYObject> classLeadingComments = new HashMap<>();
    private static Map<TYModule, TYObject> moduleLeadingComments = new HashMap<>();
    private static Map<TYMethod, TYObject> methodLeadingComments = new HashMap<>();
    private static Map<TYFieldObject, TYObject> fieldLeadingComments = new HashMap<>();
    
    private static Map<TYThread, TYThreadObject> threadObjectMap = new HashMap<>();
    
    private static TYString nilString = null;
    private static TYFloat e = null, pi = null;
    
    public synchronized static TYClassObject getClassObject(TYClass tyClass) {
        
        if (!classObjects.containsKey(tyClass)) {
            
            classObjects.put(tyClass, new TYClassObject(tyClass));
        }
        
        return classObjects.get(tyClass);
    }
    
    public synchronized static TYStaticClassObject getStaticClassObject(TYClass tyClass) {
        
        if (!staticClassObjects.containsKey(tyClass)) {
            
            staticClassObjects.put(tyClass, new TYStaticClassObject(tyClass));
        }
        
        return staticClassObjects.get(tyClass);
    }
    
    public synchronized static TYModuleObject getModuleObject(TYModule tyModule) {
        
        if (!moduleObjects.containsKey(tyModule)) {
            
            moduleObjects.put(tyModule, new TYModuleObject(tyModule));
        }
        
        return moduleObjects.get(tyModule);
    }
    
    public synchronized static TYStaticModuleObject getStaticModuleObject(TYModule tyModule) {
        
        if (!staticModuleObjects.containsKey(tyModule)) {
            
            staticModuleObjects.put(tyModule, new TYStaticModuleObject(tyModule));
        }
        
        return staticModuleObjects.get(tyModule);
    }
    
    public synchronized static TYMethodObject getMethodObject(TYMethod tyMethod) {
        
        if (!methodObjects.containsKey(tyMethod)) {
            
            methodObjects.put(tyMethod, new TYMethodObject(tyMethod));
        }
        
        return methodObjects.get(tyMethod);
    }
    
    public synchronized static TYFieldObject getFieldObject(TYClass tyClass, String name) {
        
        if (!fieldObjects.containsKey(tyClass)) {
            
            fieldObjects.put(tyClass, new HashMap<>());
        }
        
        if (!fieldObjects.get(tyClass).containsKey(name)) {
            
            fieldObjects.get(tyClass).put(name, new TYFieldObject(tyClass, name));
        }
        
        return fieldObjects.get(tyClass).get(name);
    }
    
    public synchronized static TYString getClassName(TYClass tyClass) {
        
        if (!classNames.containsKey(tyClass)) {
            
            classNames.put(tyClass, new TYString(tyClass.getName()));
        }
        
        return classNames.get(tyClass);
    }
    
    public synchronized static TYString getClassShortName(TYClass tyClass) {
        
        if (!classShortNames.containsKey(tyClass)) {
            
            classShortNames.put(tyClass, new TYString(tyClass.getShortName()));
        }
        
        return classShortNames.get(tyClass);
    }
    
    public synchronized static TYString getModuleName(TYModule tyModule) {
        
        if (!moduleNames.containsKey(tyModule)) {
            
            moduleNames.put(tyModule, new TYString(tyModule.getName()));
        }
        
        return moduleNames.get(tyModule);
    }
    
    public synchronized static TYString getModuleShortName(TYModule tyModule) {
        
        if (!moduleShortNames.containsKey(tyModule)) {
            
            moduleShortNames.put(tyModule, new TYString(tyModule.getShortName()));
        }
        
        return moduleShortNames.get(tyModule);
    }
    
    public synchronized static TYString getMethodName(TYMethod tyMethod) {
        
        if (!methodNames.containsKey(tyMethod)) {
            
            methodNames.put(tyMethod, new TYString(tyMethod.getName()));
        }
        
        return methodNames.get(tyMethod);
    }
    
    public synchronized static TYBoolean isMethodStatic(TYMethod tyMethod) {
        
        if (!methodStatic.containsKey(tyMethod)) {
            
            methodStatic.put(tyMethod, TYBoolean.valueFor(tyMethod.isStaticMethod()));
        }
        
        return methodStatic.get(tyMethod);
    }
    
    public synchronized static TYBoolean isMethodNative(TYMethod tyMethod) {
        
        if (!methodNative.containsKey(tyMethod)) {
            
            methodNative.put(tyMethod, TYBoolean.valueFor(tyMethod.isNativeMethod()));
        }
        
        return methodNative.get(tyMethod);
    }
    
    public synchronized static TYBoolean isMethodSecure(TYMethod tyMethod) {
        
        if (!methodSecure.containsKey(tyMethod)) {
            
            methodSecure.put(tyMethod, TYBoolean.valueFor(tyMethod.isSecureMethod()));
        }
        
        return methodSecure.get(tyMethod);
    }
    
    public synchronized static TYArray getMandatoryArguments(TYProcedure tyProcedure) {
        
        if (!mandatoryArguments.containsKey(tyProcedure)) {
            
            List<String> arguments = tyProcedure.getMandatoryParameters();
            
            mandatoryArguments.put(tyProcedure, TrinityNatives.getArrayFor(arguments.toArray(new String[arguments.size()])));
        }
        
        return mandatoryArguments.get(tyProcedure);
    }
    
    public synchronized static TYArray getOptionalArguments(TYProcedure tyProcedure) {
        
        if (!optionalArguments.containsKey(tyProcedure)) {
            
            Set<String> arguments = tyProcedure.getOptionalParameters().keySet();
            
            optionalArguments.put(tyProcedure, TrinityNatives.getArrayFor(arguments.toArray(new String[arguments.size()])));
        }
        
        return optionalArguments.get(tyProcedure);
    }
    
    public synchronized static TYObject getBlockArgument(TYProcedure tyProcedure) {
        
        if (!blockArguments.containsKey(tyProcedure)) {
            
            blockArguments.put(tyProcedure, tyProcedure.getBlockParameter() == null ? TYObject.NIL : new TYString(tyProcedure.getBlockParameter()));
        }
        
        return blockArguments.get(tyProcedure);
    }
    
    public synchronized static TYObject getOverflowArgument(TYProcedure tyProcedure) {
        
        if (!overflowArguments.containsKey(tyProcedure)) {
            
            overflowArguments.put(tyProcedure, tyProcedure.getOverflowParameter() == null ? TYObject.NIL : new TYString(tyProcedure.getOverflowParameter()));
        }
        
        return overflowArguments.get(tyProcedure);
    }
    
    public synchronized static TYString getFieldName(TYFieldObject fieldObject) {
        
        if (!fieldNames.containsKey(fieldObject)) {
            
            fieldNames.put(fieldObject, new TYString(fieldObject.getInternalName()));
        }
        
        return fieldNames.get(fieldObject);
    }
    
    public synchronized static TYBoolean isFieldStatic(TYFieldObject fieldObject) {
        
        if (!fieldStatic.containsKey(fieldObject)) {
            
            fieldStatic.put(fieldObject, TYBoolean.valueFor(fieldObject.isStatic()));
        }
        
        return fieldStatic.get(fieldObject);
    }
    
    public synchronized static TYBoolean isFieldNative(TYFieldObject fieldObject) {
        
        if (!fieldNative.containsKey(fieldObject)) {
            
            fieldNative.put(fieldObject, TYBoolean.valueFor(fieldObject.isNative()));
        }
        
        return fieldNative.get(fieldObject);
    }
    
    public synchronized static TYBoolean isFieldConstant(TYFieldObject fieldObject) {
        
        if (!fieldConstant.containsKey(fieldObject)) {
            
            fieldConstant.put(fieldObject, TYBoolean.valueFor(fieldObject.isConstant()));
        }
        
        return fieldConstant.get(fieldObject);
    }
    
    public synchronized static TYInt getArrayLength(TYArray tyArray) {
        
        if (!arrayLengths.containsKey(tyArray)) {
            
            arrayLengths.put(tyArray, new TYInt(tyArray.size()));
        }
        
        return arrayLengths.get(tyArray);
    }
    
    public synchronized static void clearArrayData(TYArray tyArray) {
        
        arrayLengths.remove(tyArray);
    }
    
    public synchronized static TYArray getMapKeySet(TYMap tyMap) {
        
        if (!mapKeySets.containsKey(tyMap)) {
            
            Set<TYObject> keys = tyMap.getInternalMap().keySet();
            List<TYObject> keyList = new ArrayList<>(keys);
            mapKeySets.put(tyMap, new TYArray(keyList));
        }
        
        return mapKeySets.get(tyMap);
    }
    
    public synchronized static TYArray getMapValues(TYMap tyMap) {
        
        if (!mapValues.containsKey(tyMap)) {
            
            Collection<TYObject> keys = tyMap.getInternalMap().values();
            List<TYObject> keyList = new ArrayList<>(keys);
            mapValues.put(tyMap, new TYArray(keyList));
        }
        
        return mapValues.get(tyMap);
    }
    
    public synchronized static TYInt getMapLength(TYMap tyMap) {
        
        if (!mapLengths.containsKey(tyMap)) {
            
            mapLengths.put(tyMap, new TYInt(tyMap.size()));
        }
        
        return mapLengths.get(tyMap);
    }
    
    public synchronized static void clearMapData(TYMap tyMap) {
        
        mapKeySets.remove(tyMap);
        mapValues.remove(tyMap);
        mapLengths.remove(tyMap);
    }
    
    public synchronized static TYInt getHashCode(TYObject tyObject) {
        
        if (!hashCodes.containsKey(tyObject)) {
            
            hashCodes.put(tyObject, tyObject == TYObject.NIL ? new TYInt(0) : new TYInt(tyObject.hashCode()));
        }
        
        return hashCodes.get(tyObject);
    }
    
    public synchronized static TYString getNilString() {
        
        if (nilString == null) {
            
            nilString = new TYString("nil");
        }
        
        return nilString;
    }
    
    public synchronized static TYObject getLeadingComments(TYClass tyClass) {
        
        if (!classLeadingComments.containsKey(tyClass)) {
            
            String[] c = tyClass.getLeadingComments();
            classLeadingComments.put(tyClass, TrinityNatives.getObjectFor(c));
        }
        
        return classLeadingComments.get(tyClass);
    }
    
    public synchronized static TYObject getLeadingComments(TYModule tyModule) {
        
        if (!moduleLeadingComments.containsKey(tyModule)) {
            
            String[] c = tyModule.getLeadingComments();
            moduleLeadingComments.put(tyModule, TrinityNatives.getObjectFor(c));
        }
        
        return moduleLeadingComments.get(tyModule);
    }
    
    public synchronized static TYObject getLeadingComments(TYMethod tyMethod) {
        
        if (!methodLeadingComments.containsKey(tyMethod)) {
            
            String[] c = tyMethod.getLeadingComments();
            methodLeadingComments.put(tyMethod, TrinityNatives.getObjectFor(c));
        }
        
        return methodLeadingComments.get(tyMethod);
    }
    
    public synchronized static TYObject getLeadingComments(TYFieldObject fieldObject) {
        
        if (!fieldLeadingComments.containsKey(fieldObject)) {
            
            String[] c = fieldObject.getLeadingComments();
            fieldLeadingComments.put(fieldObject, TrinityNatives.getObjectFor(c));
        }
        
        return fieldLeadingComments.get(fieldObject);
    }
    
    public synchronized static TYThreadObject getThreadObject(TYThread tyThread) {
        
        if (!threadObjectMap.containsKey(tyThread)) {
            
            threadObjectMap.put(tyThread, new TYThreadObject(tyThread));
        }
        
        return threadObjectMap.get(tyThread);
    }
    
    public synchronized static TYFloat getE() {
        
        if (e == null) {
            
            e = new TYFloat(Math.E);
        }
        
        return e;
    }
    
    public synchronized static TYFloat getPi() {
        
        if (pi == null) {
            
            pi = new TYFloat(Math.PI);
        }
        
        return pi;
    }
}
