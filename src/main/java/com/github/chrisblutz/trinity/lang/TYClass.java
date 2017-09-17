package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.interpreter.Scope;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.variables.VariableLoc;
import com.github.chrisblutz.trinity.lang.variables.VariableManager;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.plugins.PluginLoader;

import java.util.*;


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
    private List<ProcedureAction> initializationActions = new ArrayList<>();
    private boolean initialized = false;
    
    private List<String> callableMethods = new ArrayList<>();
    
    private Map<String, Boolean> variables = new HashMap<>();
    private Map<String, Boolean> variablesNative = new HashMap<>();
    private Map<String, String[]> variablesComments = new HashMap<>();
    private Map<String, Scope> classVariableScopes = new HashMap<>();
    private Map<String, Boolean> classVariableConstant = new HashMap<>();
    private Map<String, String[]> classVariableImports = new HashMap<>();
    private Map<String, ProcedureAction> classVariableActions = new HashMap<>();
    private Map<String, VariableLoc> classVariables = new HashMap<>();
    private Map<String, Scope> instanceVariableScopes = new HashMap<>();
    private Map<String, Boolean> instanceVariableConstant = new HashMap<>();
    private Map<String, String[]> instanceVariableImports = new HashMap<>();
    private Map<String, ProcedureAction> instanceVariableActions = new HashMap<>();
    private Map<TYObject, Map<String, VariableLoc>> instanceVariables = new WeakHashMap<>();
    
    private String[] leadingComments = null;
    
    public TYClass(String name, String shortName) {
        
        this(name, shortName, name.contentEquals(TrinityNatives.Classes.OBJECT) ? null : ClassRegistry.getClass(TrinityNatives.Classes.OBJECT));
    }
    
    public TYClass(String name, String shortName, TYClass superclass) {
        
        this.name = name;
        this.shortName = shortName;
        this.superclass = superclass;
        
        inheritanceTree = compileInheritanceTree();
        inheritanceTree.add(this);
    }
    
    private List<TYClass> compileInheritanceTree() {
        
        List<TYClass> tree = new ArrayList<>();
        
        if (superclass != null) {
            
            tree.add(superclass);
            
            tree.addAll(superclass.compileInheritanceTree());
        }
        
        return tree;
    }
    
    private Set<String> compileCallableMethods() {
        
        Set<String> methods = new LinkedHashSet<>();
        
        methods.addAll(getMethodNames());
        
        if (superclass != null) {
            
            methods.addAll(superclass.compileCallableMethods());
        }
        
        return methods;
    }
    
    public void registerClassVariable(String name, boolean isNative, String[] leadingComments, ProcedureAction action, Scope scope, boolean constant, String[] importedModules) {
        
        variables.put(name, true);
        variablesNative.put(name, isNative);
        variablesComments.put(name, leadingComments);
        classVariableActions.put(name, action);
        classVariableScopes.put(name, scope);
        classVariableConstant.put(name, constant);
        classVariableImports.put(name, importedModules);
    }
    
    public void initializeClassFields(TYRuntime runtime) {
        
        for (String str : classVariableActions.keySet()) {
            
            ProcedureAction action = classVariableActions.get(str);
            
            TYObject val = TYObject.NIL;
            if (action != null) {
                
                TYRuntime newRuntime = runtime.clone();
                newRuntime.clearVariables();
                
                newRuntime.setScope(NativeStorage.getClassObject(this), true);
                newRuntime.setScopeClass(this);
                newRuntime.setModule(getModule());
                newRuntime.setTyClass(this);
                newRuntime.importModules(classVariableImports.get(str));
                
                val = classVariableActions.get(str).onAction(newRuntime, TYObject.NONE);
            }
            
            VariableLoc loc = new VariableLoc();
            loc.setContainerClass(this);
            loc.setScope(classVariableScopes.get(str));
            loc.setConstant(classVariableConstant.get(str));
            VariableManager.put(loc, val);
            classVariables.put(str, loc);
        }
    }
    
    public void registerInstanceVariable(String name, boolean isNative, String[] leadingComments, ProcedureAction action, Scope scope, boolean constant, String[] importedModules) {
        
        variables.put(name, false);
        variablesNative.put(name, isNative);
        variablesComments.put(name, leadingComments);
        instanceVariableActions.put(name, action);
        instanceVariableScopes.put(name, scope);
        instanceVariableConstant.put(name, constant);
        instanceVariableImports.put(name, importedModules);
    }
    
    public void initializeInstanceFields(TYObject object, TYRuntime runtime) {
        
        instanceVariables.put(object, new HashMap<>());
        Map<String, VariableLoc> varMap = instanceVariables.get(object);
        
        for (String str : instanceVariableActions.keySet()) {
            
            ProcedureAction action = instanceVariableActions.get(str);
            
            TYObject val = TYObject.NIL;
            if (action != null) {
                
                TYRuntime newRuntime = runtime.clone();
                newRuntime.clearVariables();
                newRuntime.setThis(object);
                newRuntime.setScope(object, false);
                newRuntime.setModule(getModule());
                newRuntime.setTyClass(this);
                newRuntime.importModules(instanceVariableImports.get(str));
                
                val = instanceVariableActions.get(str).onAction(newRuntime, TYObject.NONE);
            }
            
            VariableLoc loc = new VariableLoc();
            loc.setContainerClass(this);
            loc.setScope(instanceVariableScopes.get(str));
            loc.setConstant(instanceVariableConstant.get(str));
            VariableManager.put(loc, val);
            varMap.put(str, loc);
        }
        
        if (superclass != null) {
            
            superclass.initializeInstanceFields(object, runtime);
        }
    }
    
    public boolean hasVariable(String name) {
        
        for (String var : classVariables.keySet()) {
            
            if (var.contentEquals(name)) {
                
                return true;
            }
        }
        
        return superclass != null && superclass.hasVariable(name);
    }
    
    public VariableLoc getVariable(String name) {
        
        for (String var : classVariables.keySet()) {
            
            if (var.contentEquals(name)) {
                
                return classVariables.get(var);
            }
        }
        
        if (superclass != null) {
            
            return superclass.getVariable(name);
        }
        
        return null;
    }
    
    public boolean hasVariable(String name, TYObject thisObj) {
        
        if (instanceVariables.containsKey(thisObj)) {
            
            for (String var : instanceVariables.get(thisObj).keySet()) {
                
                if (var.contentEquals(name)) {
                    
                    return true;
                }
            }
        }
        
        for (String var : classVariables.keySet()) {
            
            if (var.contentEquals(name)) {
                
                return true;
            }
        }
        
        return superclass != null && superclass.hasVariable(name, thisObj);
    }
    
    public VariableLoc getVariable(String name, TYObject thisObj) {
        
        if (instanceVariables.containsKey(thisObj)) {
            
            for (String var : instanceVariables.get(thisObj).keySet()) {
                
                if (var.contentEquals(name)) {
                    
                    return instanceVariables.get(thisObj).get(var);
                }
            }
        }
        
        for (String var : classVariables.keySet()) {
            
            if (var.contentEquals(name)) {
                
                return classVariables.get(var);
            }
        }
        
        if (superclass != null) {
            
            return superclass.getVariable(name, thisObj);
        }
        
        return null;
    }
    
    public String[] getFieldArray() {
        
        return variables.keySet().toArray(new String[variables.size()]);
    }
    
    public boolean fieldExists(String name) {
        
        return variables.keySet().contains(name);
    }
    
    public boolean isFieldNative(String name) {
        
        return variablesNative.getOrDefault(name, false);
    }
    
    public boolean isFieldStatic(String name) {
        
        return variables.getOrDefault(name, false);
    }
    
    public boolean isFieldConstant(String name) {
        
        if (isFieldStatic(name)) {
            
            return classVariableConstant.getOrDefault(name, false);
            
        } else {
            
            return instanceVariableConstant.getOrDefault(name, false);
        }
    }
    
    public String[] getFieldLeadingComments(String name) {
        
        return variablesComments.getOrDefault(name, null);
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
        
        runInitializationActions();
        
        if (methodName.contentEquals("new")) {
            
            if (constructor == null) {
                
                TYRuntime newRuntime = runtime.clone();
                newRuntime.clearVariables();
                
                TYObject newObj = new TYObject(this);
                
                newRuntime.setThis(newObj);
                newRuntime.setScope(newObj, false);
                newRuntime.setModule(getModule());
                newRuntime.setTyClass(this);
                
                initializeInstanceFields(newObj, newRuntime);
                
                return newObj;
                
            } else {
                
                Scope scope = constructor.getScope();
                boolean run = checkScope(scope, constructor, runtime);
                
                if (run) {
                    
                    TYRuntime newRuntime = runtime.clone();
                    newRuntime.clearVariables();
                    
                    TYObject newObj = new TYObject(this);
                    
                    newRuntime.setThis(newObj);
                    newRuntime.setScope(newObj, false);
                    newRuntime.setModule(getModule());
                    newRuntime.setTyClass(this);
                    newRuntime.importModules(constructor.getImportedModules());
                    
                    initializeInstanceFields(newObj, newRuntime);
                    
                    TYObject obj = constructor.getProcedure().call(newRuntime, procedure, procedureRuntime, newObj, params);
                    
                    if (newRuntime.isReturning()) {
                        
                        Errors.throwError(Errors.Classes.RETURN_ERROR, runtime, "Cannot return a value from a constructor.");
                        
                    } else if (TrinityNatives.isClassNativelyConstructed(newObj.getObjectClass()) && TrinityNatives.isClassNativelyConstructed(obj.getObjectClass())) {
                        
                        newObj = obj;
                    }
                    
                    return newObj;
                    
                } else {
                    
                    Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Constructor cannot be accessed from this context because it is marked '" + scope.toString() + "'.");
                    
                    return TYObject.NONE;
                }
            }
            
        } else if (methods.containsKey(methodName)) {
            
            TYMethod method = methods.get(methodName);
            
            Scope scope = method.getScope();
            boolean run = checkScope(scope, method, runtime);
            
            if (run) {
                
                TYRuntime newRuntime = runtime.clone();
                newRuntime.setModule(getModule());
                newRuntime.setTyClass(this);
                newRuntime.importModules(method.getImportedModules());
                newRuntime.clearVariables();
                
                if (method.isStaticMethod()) {
                    
                    newRuntime.setScope(NativeStorage.getClassObject(this), true);
                    newRuntime.setScopeClass(this);
                    
                } else {
                    
                    if (thisObj == TYObject.NONE) {
                        
                        Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Instance method '" + methodName + "' cannot be called from a static context.");
                    }
                    
                    newRuntime.setThis(thisObj);
                    newRuntime.setScope(thisObj, false);
                }
                
                TYObject result = method.getProcedure().call(newRuntime, procedure, procedureRuntime, thisObj, params);
                
                if (newRuntime.isReturning()) {
                    
                    return newRuntime.getReturnObject();
                }
                
                return result;
                
            } else {
                
                Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Method '" + methodName + "' cannot be accessed from this context because it is marked '" + scope.toString() + "'.");
                
                return TYObject.NONE;
            }
            
        } else if (getSuperclass() != null) {
            
            return getSuperclass().tyInvoke(originClass, methodName, runtime, procedure, procedureRuntime, thisObj, params);
            
        } else if ((!runtime.isStaticScope() || thisObj == TYObject.NONE) && ClassRegistry.getClass(TrinityNatives.Classes.KERNEL).getMethods().containsKey(methodName)) {
            
            return ClassRegistry.getClass(TrinityNatives.Classes.KERNEL).tyInvoke(originClass, methodName, runtime, procedure, procedureRuntime, thisObj, params);
            
        } else {
            
            Errors.throwError(Errors.Classes.METHOD_NOT_FOUND_ERROR, runtime, "No method '" + methodName + "' found in '" + originClass.getName() + "'.");
        }
        
        return TYObject.NONE;
    }
    
    private boolean checkScope(Scope scope, TYMethod method, TYRuntime runtime) {
        
        switch (scope) {
            
            case PUBLIC:
                
                return true;
            
            case MODULE_PROTECTED:
                
                return method.getContainerClass().getModule() == runtime.getModule();
            
            case PROTECTED:
                
                return runtime.getTyClass().isInstanceOf(method.getContainerClass());
            
            case PRIVATE:
                
                return method.getContainerClass() == runtime.getTyClass();
            
            default:
                
                return false;
        }
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
        
        PluginLoader.triggerOnMethodUpdate(this, method);
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
    
    public Collection<String> getMethodNames() {
        
        return methods.keySet();
    }
    
    public boolean respondsTo(String method) {
        
        return callableMethods.contains(method);
    }
    
    public String[] getLeadingComments() {
        
        return leadingComments;
    }
    
    public void setLeadingComments(String[] leadingComments) {
        
        this.leadingComments = leadingComments;
    }
    
    public void addInitializationAction(ProcedureAction action) {
        
        initializationActions.add(action);
    }
    
    public void runInitializationActions() {
        
        if (!initialized) {
            
            initialized = true;
            
            TYRuntime runtime = new TYRuntime();
            
            initializeClassFields(runtime);
            
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
                        
                        Errors.throwError(Errors.Classes.CLASS_NOT_FOUND_ERROR, "Class " + superclassString + " does not exist.");
                    }
                }
            }
            
            inheritanceTree = compileInheritanceTree();
            inheritanceTree.add(this);
        }
        
        
        Set<String> callables = compileCallableMethods();
        callableMethods = new ArrayList<>(callables);
        Collections.sort(callableMethods);
    }
}
