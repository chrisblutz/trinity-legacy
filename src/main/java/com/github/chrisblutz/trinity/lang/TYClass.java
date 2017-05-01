package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.DefaultProcedures;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TYClass {
    
    private List<TYClass> classes = new ArrayList<>();
    private String name, shortName;
    private TYMethod constructor;
    private TYClass superclass;
    private TYModule module;
    private List<TYClass> inheritanceTree = new ArrayList<>();
    private Map<String, TYMethod> methods = new HashMap<>();
    private Map<String, TYObject> variables = new HashMap<>();
    
    public TYClass(String name, String shortName) {
        
        this(name, shortName, name.contentEquals("Object") ? null : ClassRegistry.getClass("Object"));
    }
    
    public TYClass(String name, String shortName, TYClass superclass) {
        
        this.name = name;
        this.shortName = shortName;
        this.superclass = superclass;
        
        inheritanceTree = compileInheritanceTree();
        inheritanceTree.add(this);
        
        registerMethod(new TYMethod("+", false, true, this, DefaultProcedures.getDefaultUOEOperationProcedure("+")));
        registerMethod(new TYMethod("-", false, true, this, DefaultProcedures.getDefaultUOEOperationProcedure("-")));
        registerMethod(new TYMethod("*", false, true, this, DefaultProcedures.getDefaultUOEOperationProcedure("*")));
        registerMethod(new TYMethod("/", false, true, this, DefaultProcedures.getDefaultUOEOperationProcedure("/")));
        registerMethod(new TYMethod("%", false, true, this, DefaultProcedures.getDefaultUOEOperationProcedure("%")));
        registerMethod(new TYMethod("compareTo", false, true, this, DefaultProcedures.getDefaultUOEOperationProcedure("compareTo")));
        registerMethod(new TYMethod("==", false, true, this, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                TYObject object = params[0];
                
                TYInt thisHashCode = TrinityNatives.cast(TYInt.class, thisObj.tyInvoke("hashCode", runtime, stackTrace, null, null), stackTrace);
                TYInt otherHashCode = TrinityNatives.cast(TYInt.class, object.tyInvoke("hashCode", runtime, stackTrace, null, null), stackTrace);
                
                return thisHashCode.tyInvoke("==", runtime, stackTrace, null, null, otherHashCode);
                
            } else {
                
                return TYBoolean.FALSE;
            }
        })));
    }
    
    private List<TYClass> compileInheritanceTree() {
        
        List<TYClass> tree = new ArrayList<>();
        
        if (superclass != null) {
            
            tree.add(superclass);
            
            tree.addAll(superclass.compileInheritanceTree());
        }
        
        return tree;
    }
    
    public Map<String, TYObject> getVariables() {
        
        return variables;
    }
    
    public String getName() {
        
        return name;
    }
    
    public String getShortName() {
        
        return shortName;
    }
    
    public TYClass getSuperclass() {
        
        return superclass;
    }
    
    public void setSuperclass(TYClass superclass) {
        
        this.superclass = superclass;
        inheritanceTree = compileInheritanceTree();
        inheritanceTree.add(this);
    }
    
    public boolean isInstanceOf(TYClass tyClass) {
        
        return inheritanceTree.contains(tyClass);
    }
    
    public void addClass(TYClass tyClass) {
        
        classes.add(tyClass);
    }
    
    public List<TYClass> getClasses() {
        
        return classes;
    }
    
    public boolean hasClass(String shortName) {
        
        for (TYClass tyClass : getClasses()) {
            
            if (tyClass.getShortName().contentEquals(shortName)) {
                
                return true;
            }
        }
        
        return false;
    }
    
    public TYClass getClass(String shortName) {
        
        for (TYClass tyClass : getClasses()) {
            
            if (tyClass.getShortName().contentEquals(shortName)) {
                
                return tyClass;
            }
        }
        
        return null;
    }
    
    public TYModule getModule() {
        
        return module;
    }
    
    public void setModule(TYModule module) {
        
        this.module = module;
    }
    
    public TYObject tyInvoke(String methodName, TYRuntime runtime, TYStackTrace stackTrace, TYProcedure procedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
        return tyInvoke(this, methodName, runtime, stackTrace, procedure, procedureRuntime, thisObj, params);
    }
    
    public TYObject tyInvoke(TYClass originClass, String methodName, TYRuntime runtime, TYStackTrace stackTrace, TYProcedure procedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
        if (methodName.contentEquals("new")) {
            
            if (constructor != null) {
                
                TYRuntime newRuntime = runtime.clone();
                newRuntime.clearVariables();
                
                TYObject newObj = new TYObject(this);
                
                newRuntime.setVariable("this", newObj);
                newRuntime.setScope(newObj, false);
                newRuntime.setModule(getModule());
                newRuntime.setTyClass(this);
                newRuntime.importModules(constructor.getImportedModules());
                
                TYObject obj = constructor.getProcedure().call(newRuntime, stackTrace, procedure, procedureRuntime, newObj, params);
                
                if (newRuntime.isReturning()) {
                    
                    TYError error = new TYError("Trinity.Errors.ReturnError", "Cannot return a value from a constructor.", stackTrace);
                    error.throwError();
                    
                } else if (obj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Map")) || obj.getObjectClass().isInstanceOf(ClassRegistry.getClass("Procedure"))) {
                    
                    newObj = obj;
                }
                
                return newObj;
                
            } else {
                
                return new TYObject(this);
            }
            
        } else if (methods.containsKey(methodName)) {
            
            TYMethod method = methods.get(methodName);
            
            TYRuntime newRuntime = runtime.clone();
            newRuntime.setModule(getModule());
            newRuntime.setTyClass(this);
            newRuntime.importModules(method.getImportedModules());
            newRuntime.clearVariables();
            
            if (method.isStaticMethod()) {
                
                newRuntime.setScope(NativeStorage.getClassObject(this), true);
                
            } else {
                
                if (thisObj == TYObject.NONE) {
                    
                    TYError error = new TYError("Trinity.Errors.ScopeError", "Instance method '" + methodName + "' cannot be called from a static context.", stackTrace);
                    error.throwError();
                }
                
                newRuntime.setVariable("this", thisObj);
                newRuntime.setScope(thisObj, false);
            }
            
            TYObject result = method.getProcedure().call(newRuntime, stackTrace, procedure, procedureRuntime, thisObj, params);
            
            if (newRuntime.isReturning()) {
                
                return newRuntime.getReturnObject();
            }
            
            return result;
            
        } else if (getSuperclass() != null) {
            
            return getSuperclass().tyInvoke(originClass, methodName, runtime, stackTrace, procedure, procedureRuntime, thisObj, params);
            
        } else if (ClassRegistry.getClass("Kernel").getMethods().containsKey(methodName)) {
            
            return ClassRegistry.getClass("Kernel").tyInvoke(originClass, methodName, runtime, stackTrace, procedure, procedureRuntime, thisObj, params);
            
        } else {
            
            TYError notFoundError = new TYError("Trinity.Errors.MethodNotFoundError", "No method '" + methodName + "' found in '" + originClass.getName() + "'.", stackTrace);
            notFoundError.throwError();
        }
        
        return TYObject.NONE;
    }
    
    public void registerMethod(TYMethod method) {
        
        if (method.getName().contentEquals("initialize")) {
            
            constructor = method;
            
            methods.put(method.getName(), method);
            
        } else {
            
            methods.put(method.getName(), method);
            
            if (method.getName().contentEquals("main")) {
                
                ClassRegistry.registerMainClass(this);
            }
        }
    }
    
    public Map<String, TYMethod> getMethods() {
        
        return methods;
    }
    
    public TYMethod[] getMethodArray() {
        
        return methods.values().toArray(new TYMethod[methods.values().size()]);
    }
    
    public TYMethod getMethod(String name) {
        
        return getMethods().getOrDefault(name, null);
    }
}
