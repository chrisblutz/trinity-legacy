package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.DefaultProcedures;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.natives.NativeStorage;

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
    private String superclassString;
    private String[] importedForSuperclass;
    private TYModule module;
    private List<TYClass> inheritanceTree = new ArrayList<>();
    private Map<String, TYMethod> methods = new HashMap<>();
    private Map<String, TYObject> variables = new HashMap<>();
    private List<ProcedureAction> initializationActions = new ArrayList<>();
    private boolean initialized = false;
    
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
    }
    
    public void setSuperclassString(String string, String[] imports) {
        
        this.superclassString = string;
        this.importedForSuperclass = imports;
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
    
    public TYObject tyInvoke(String methodName, TYRuntime runtime, TYProcedure procedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
        return tyInvoke(this, methodName, runtime, procedure, procedureRuntime, thisObj, params);
    }
    
    public TYObject tyInvoke(TYClass originClass, String methodName, TYRuntime runtime, TYProcedure procedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
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
                
                TYObject obj = constructor.getProcedure().call(newRuntime, procedure, procedureRuntime, newObj, params);
                
                if (newRuntime.isReturning()) {
                    
                    Errors.throwError("Trinity.Errors.ReturnError", "Cannot return a value from a constructor.", runtime);
                    
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
                    
                    Errors.throwError("Trinity.Errors.ScopeError", "Instance method '" + methodName + "' cannot be called from a static context.", runtime);
                }
                
                newRuntime.setVariable("this", thisObj);
                newRuntime.setScope(thisObj, false);
            }
            
            TYObject result = method.getProcedure().call(newRuntime, procedure, procedureRuntime, thisObj, params);
            
            if (newRuntime.isReturning()) {
                
                return newRuntime.getReturnObject();
            }
            
            return result;
            
        } else if (getSuperclass() != null) {
            
            return getSuperclass().tyInvoke(originClass, methodName, runtime, procedure, procedureRuntime, thisObj, params);
            
        } else if (ClassRegistry.getClass("Kernel").getMethods().containsKey(methodName)) {
            
            return ClassRegistry.getClass("Kernel").tyInvoke(originClass, methodName, runtime, procedure, procedureRuntime, thisObj, params);
            
        } else {
            
            Errors.throwError("Trinity.Errors.MethodNotFoundError", "No method '" + methodName + "' found in '" + originClass.getName() + "'.", runtime);
        }
        
        return TYObject.NONE;
    }
    
    public void registerMethod(TYMethod method) {
        
        if (methods.containsKey(method.getName()) && methods.get(method.getName()).isSecureMethod()) {
            
            return;
        }
        
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
    
    public void addInitializationActions(List<ProcedureAction> actions) {
        
        initializationActions.addAll(actions);
    }
    
    public void runInitializationActions(TYRuntime runtime) {
        
        if (!initialized) {
            
            initialized = true;
            
            for (ProcedureAction action : initializationActions) {
                
                action.onAction(runtime, TYObject.NONE);
            }
        }
    }
    
    public void performFinalSetup() {
        
        if (superclassString != null) {
            
            if (module != null && module.hasClass(superclassString)) {
                
                setSuperclass(module.getClass(superclassString));
                
            } else {
                
                boolean found = false;
                
                for (String modStr : importedForSuperclass) {
                    
                    TYModule module = ModuleRegistry.getModule(modStr);
                    
                    if (module.hasClass(superclassString)) {
                        
                        found = true;
                        setSuperclass(module.getClass(superclassString));
                        break;
                    }
                }
                
                if (!found) {
                    
                    if (ClassRegistry.classExists(superclassString)) {
                        
                        setSuperclass(ClassRegistry.getClass(superclassString));
                        
                    } else {
                        
                        Errors.throwError("Trinity.Errors.ClassNotFoundError", "Class " + superclassString + " does not exist.");
                    }
                }
            }
            
            inheritanceTree = compileInheritanceTree();
            inheritanceTree.add(this);
        }
    }
}
