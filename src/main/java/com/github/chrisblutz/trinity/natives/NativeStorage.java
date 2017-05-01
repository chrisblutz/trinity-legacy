package com.github.chrisblutz.trinity.natives;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYMethod;
import com.github.chrisblutz.trinity.lang.TYModule;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.*;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.maps.TYMap;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;

import java.util.*;


/**
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
    
    private static Map<TYMethod, TYArray> mandatoryArguments = new HashMap<>();
    private static Map<TYMethod, TYArray> optionalArguments = new HashMap<>();
    private static Map<TYMethod, TYObject> blockArguments = new HashMap<>();
    
    private static Map<TYArray, TYInt> arrayLengths = new HashMap<>();
    
    private static Map<TYMap, TYArray> mapKeySets = new HashMap<>();
    private static Map<TYMap, TYArray> mapValues = new HashMap<>();
    private static Map<TYMap, TYInt> mapLengths = new HashMap<>();
    
    private static Map<TYObject, TYInt> hashCodes = new HashMap<>();
    
    private static TYString nilString = null;
    
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
    
    public static TYArray getMandatoryArguments(TYMethod tyMethod) {
        
        if (!mandatoryArguments.containsKey(tyMethod)) {
            
            List<String> arguments = tyMethod.getProcedure().getMandatoryParameters();
            
            mandatoryArguments.put(tyMethod, TrinityNatives.getArrayFor(arguments.toArray(new String[arguments.size()])));
        }
        
        return mandatoryArguments.get(tyMethod);
    }
    
    public static TYArray getOptionalArguments(TYMethod tyMethod) {
        
        if (!optionalArguments.containsKey(tyMethod)) {
            
            Set<String> arguments = tyMethod.getProcedure().getOptionalParameters().keySet();
            
            optionalArguments.put(tyMethod, TrinityNatives.getArrayFor(arguments.toArray(new String[arguments.size()])));
        }
        
        return optionalArguments.get(tyMethod);
    }
    
    public static TYObject getBlockArgument(TYMethod tyMethod) {
        
        if (!blockArguments.containsKey(tyMethod)) {
            
            blockArguments.put(tyMethod, tyMethod.getProcedure().getBlockParameter() == null ? TYObject.NIL : new TYString(tyMethod.getProcedure().getBlockParameter()));
        }
        
        return blockArguments.get(tyMethod);
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
}
