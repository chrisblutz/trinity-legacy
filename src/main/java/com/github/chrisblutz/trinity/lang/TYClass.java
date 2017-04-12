package com.github.chrisblutz.trinity.lang;

import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.privelages.TYPrivileges;
import com.github.chrisblutz.trinity.lang.procedures.DefaultProcedures;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYClassObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYInvalidArgumentNumberError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYMethodNotFoundError;
import com.github.chrisblutz.trinity.lang.types.errors.runtime.TYScopeError;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TYClass {
    
    public static final TYClass NATIVE = new TYClass("<native>", "<native>", null);
    
    private List<TYClass> classes = new ArrayList<>();
    private String name, shortName;
    private TYPrivileges privileges;
    private TYMethod constructor;
    private TYClass superclass;
    private TYModule module;
    private List<TYClass> inheritanceTree = new ArrayList<>();
    private Map<String, TYMethod> methods = new HashMap<>();
    private Map<String, TYObject> variables = new HashMap<>();
    
    public TYClass(String name, String shortName, TYPrivileges privileges) {
        
        this(name, shortName, privileges, name.contentEquals("Object") ? null : ClassRegistry.getClass("Object"));
    }
    
    public TYClass(String name, String shortName, TYPrivileges privileges, TYClass superclass) {
        
        this.name = name;
        this.shortName = shortName;
        this.privileges = privileges;
        this.superclass = superclass;
        
        inheritanceTree = compileInheritanceTree();
        inheritanceTree.add(this);
        
        registerMethod(new TYMethod("+", false, null, DefaultProcedures.getDefaultUOEOperationProcedure("+")));
        registerMethod(new TYMethod("-", false, null, DefaultProcedures.getDefaultUOEOperationProcedure("-")));
        registerMethod(new TYMethod("*", false, null, DefaultProcedures.getDefaultUOEOperationProcedure("*")));
        registerMethod(new TYMethod("/", false, null, DefaultProcedures.getDefaultUOEOperationProcedure("/")));
        registerMethod(new TYMethod("%", false, null, DefaultProcedures.getDefaultUOEOperationProcedure("%")));
        registerMethod(new TYMethod("compareTo", false, null, DefaultProcedures.getDefaultUOEOperationProcedure("compareTo")));
        registerMethod(new TYMethod("hashCode", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> thisObj == TYObject.NIL ? new TYInt(0) : new TYInt(thisObj.hashCode()))));
        registerMethod(new TYMethod("getClass", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> new TYClassObject(thisObj.getObjectClass()))));
        registerMethod(new TYMethod("==", false, null, new TYProcedure((runtime, stackTrace, thisObj, params) -> {
            
            if (params.length > 0) {
                
                TYObject object = params[0];
                
                TYInt thisHashCode = (TYInt) thisObj.tyInvoke("hashCode", runtime, stackTrace);
                TYInt otherHashCode = (TYInt) object.tyInvoke("hashCode", runtime, stackTrace);
                
                return thisHashCode.tyInvoke("==", runtime, stackTrace, otherHashCode);
                
            } else {
                
                return TYBoolean.FALSE;
            }
        })));
    }
    
    public List<TYClass> compileInheritanceTree() {
        
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
    
    public TYPrivileges getPrivileges() {
        
        return privileges;
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
    
    public TYObject tyInvoke(String methodName, TYRuntime runtime, TYStackTrace stackTrace, TYObject thisObj, TYObject... params) {
        
        return tyInvoke(this, methodName, runtime, stackTrace, thisObj, params);
    }
    
    public TYObject tyInvoke(TYClass originClass, String methodName, TYRuntime runtime, TYStackTrace stackTrace, TYObject thisObj, TYObject... params) {
        
        if (methodName.contentEquals("new")) {
            
            if (constructor != null) {
                
                TYRuntime newRuntime = runtime.clone();
                newRuntime.clearVariables();
                
                for (String opt : constructor.getOptionalParameters().keySet()) {
                    
                    newRuntime.setVariable(opt, constructor.getOptionalParameters().get(opt));
                }
                
                int mandatoryNum = constructor.getMandatoryParameters().size();
                int optNum = constructor.getOptionalParameters().size();
                
                if (params.length >= constructor.getMandatoryParameters().size()) {
                    
                    int paramPos;
                    for (paramPos = 0; paramPos < mandatoryNum; paramPos++) {
                        
                        newRuntime.setVariable(constructor.getMandatoryParameters().get(paramPos), params[paramPos]);
                    }
                    
                    for (; paramPos < mandatoryNum + optNum && paramPos < params.length; paramPos++) {
                        
                        if (params[paramPos] != TYObject.NONE) {
                            
                            String param = new ArrayList<>(constructor.getOptionalParameters().keySet()).get(paramPos - mandatoryNum);
                            newRuntime.setVariable(param, params[paramPos]);
                        }
                    }
                    
                } else {
                    
                    TYError error = new TYError(new TYInvalidArgumentNumberError(), "Constructor takes " + constructor.getMandatoryParameters().size() + " parameter(s).", stackTrace);
                    error.throwError();
                }
                
                TYObject newObj = new TYObject(this);
                
                newRuntime.setVariable("this", newObj);
                newRuntime.setScope(newObj, false);
                newRuntime.setModule(getModule());
                newRuntime.setTyClass(this);
                newRuntime.importModules(constructor.getImportedModules());
                
                constructor.getProcedure().call(newRuntime, stackTrace, newObj, params);
                
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
            
            for (String opt : method.getOptionalParameters().keySet()) {
                
                newRuntime.setVariable(opt, method.getOptionalParameters().get(opt));
            }
            
            if (method.isStaticMethod()) {
                
                newRuntime.setScope(new TYClassObject(this), true);
                
            } else {
                
                if (thisObj == TYObject.NONE) {
                    
                    TYError error = new TYError(new TYScopeError(), "Instance method '" + methodName + "' cannot be called from a static context.", stackTrace);
                    error.throwError();
                }
                
                newRuntime.setVariable("this", thisObj);
                newRuntime.setScope(thisObj, false);
            }
            
            int mandatoryNum = method.getMandatoryParameters().size();
            int optNum = method.getOptionalParameters().size();
            
            if (params.length >= method.getMandatoryParameters().size()) {
                
                int paramPos;
                for (paramPos = 0; paramPos < mandatoryNum; paramPos++) {
                    
                    newRuntime.setVariable(method.getMandatoryParameters().get(paramPos), params[paramPos]);
                }
                
                for (; paramPos < mandatoryNum + optNum && paramPos < params.length; paramPos++) {
                    
                    if (params[paramPos] != TYObject.NONE) {
                        
                        String param = new ArrayList<>(method.getOptionalParameters().keySet()).get(paramPos - mandatoryNum);
                        newRuntime.setVariable(param, params[paramPos]);
                    }
                }
                
            } else {
                
                TYError error = new TYError(new TYInvalidArgumentNumberError(), "Method '" + getName() + "." + methodName + "' takes " + method.getMandatoryParameters().size() + " parameter(s).", stackTrace);
                error.throwError();
            }
            
            return method.getProcedure().call(newRuntime, stackTrace, thisObj, params);
            
        } else if (getSuperclass() != null) {
            
            return getSuperclass().tyInvoke(originClass, methodName, runtime, stackTrace, thisObj, params);
            
        } else if (ClassRegistry.getClass("Kernel").getMethods().containsKey(methodName)) {
            
            return ClassRegistry.getClass("Kernel").tyInvoke(originClass, methodName, runtime, stackTrace, thisObj, params);
            
        } else {
            
            TYError notFoundError = new TYError(new TYMethodNotFoundError(), "No method '" + methodName + "' found in '" + originClass.getName() + "'.", stackTrace);
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
}
