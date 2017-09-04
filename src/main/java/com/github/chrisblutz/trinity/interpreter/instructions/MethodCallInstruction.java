package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class MethodCallInstruction extends Instruction {
    
    private String name;
    private InstructionSet[] parameters;
    private TYProcedure procedure;
    
    public MethodCallInstruction(String name, InstructionSet[] parameters, TYProcedure procedure, Location location) {
        
        super(location);
        
        this.name = name;
        this.parameters = parameters;
        this.procedure = procedure;
    }
    
    public String getName() {
        
        return name;
    }
    
    public InstructionSet[] getParameters() {
        
        return parameters;
    }
    
    public TYProcedure getProcedure() {
        
        return procedure;
    }
    
    @Override
    protected TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        List<TYObject> params = new ArrayList<>();
        for (InstructionSet param : getParameters()) {
            
            params.add(param.evaluate(TYObject.NONE, runtime));
        }
        
        TYObject[] paramArray = params.toArray(new TYObject[params.size()]);
        
        if (thisObj == TYObject.NONE) {
            
            if (runtime.isStaticScope()) {
                
                return runtime.getScopeClass().tyInvoke(getName(), runtime, getProcedure(), runtime, TYObject.NONE, paramArray);
                
            } else {
                
                return runtime.getScope().tyInvoke(getName(), runtime, getProcedure(), runtime, paramArray);
            }
            
        } else if (thisObj instanceof TYStaticClassObject) {
            
            TYStaticClassObject classObject = (TYStaticClassObject) thisObj;
            TYClass tyClass = classObject.getInternalClass();
            
            tyClass.runInitializationActions();
            
            return tyClass.tyInvoke(getName(), runtime, getProcedure(), runtime, TYObject.NONE, paramArray);
            
        } else {
            
            return thisObj.tyInvoke(getName(), runtime, getProcedure(), runtime, paramArray);
        }
    }
}
