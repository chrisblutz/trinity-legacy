package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.lang.types.procedures.TYProcedureObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class TYProcedure {
    
    private ProcedureAction procedureAction;
    private List<String> mandatoryParameters = new ArrayList<>();
    private Map<String, ProcedureAction> optionalParameters = new HashMap<>();
    private String blockParameter = null, overflowParameter = null;
    
    public TYProcedure(ProcedureAction procedureAction) {
        
        this(procedureAction, new ArrayList<>(), new HashMap<>(), null);
    }
    
    public TYProcedure(ProcedureAction procedureAction, List<String> mandatoryParameters, Map<String, ProcedureAction> optionalParameters, String blockParameter) {
        
        this(procedureAction, mandatoryParameters, optionalParameters, blockParameter, null);
    }
    
    public TYProcedure(ProcedureAction procedureAction, List<String> mandatoryParameters, Map<String, ProcedureAction> optionalParameters, String blockParameter, String overflowParameter) {
        
        this.procedureAction = procedureAction;
        this.mandatoryParameters = mandatoryParameters;
        this.optionalParameters = optionalParameters;
        this.blockParameter = blockParameter;
        this.overflowParameter = overflowParameter;
    }
    
    public ProcedureAction getProcedureAction() {
        
        return procedureAction;
    }
    
    public List<String> getMandatoryParameters() {
        
        return mandatoryParameters;
    }
    
    public Map<String, ProcedureAction> getOptionalParameters() {
        
        return optionalParameters;
    }
    
    public String getBlockParameter() {
        
        return blockParameter;
    }
    
    public void setBlockParameter(String blockParameter) {
        
        this.blockParameter = blockParameter;
    }
    
    public String getOverflowParameter() {
        
        return overflowParameter;
    }
    
    public void setOverflowParameter(String overflowParameter) {
        
        this.overflowParameter = overflowParameter;
    }
    
    public TYObject call(TYRuntime runtime, TYProcedure subProcedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
        for (String opt : getOptionalParameters().keySet()) {
            
            ProcedureAction action = getOptionalParameters().get(opt);
            runtime.setVariable(opt, action.onAction(runtime, TYObject.NONE));
        }
        
        boolean blockFlag = true;
        
        if (getBlockParameter() != null) {
            
            TYObject obj;
            
            if (subProcedure != null) {
                
                obj = new TYProcedureObject(subProcedure, procedureRuntime);
                runtime.setVariable(getBlockParameter(), obj);
                blockFlag = false;
                
            } else if (!runtime.hasVariable(getBlockParameter())) {
                
                obj = new TYProcedureObject(new TYProcedure((runtime1, thisObj1, params1) -> TYObject.NONE), new TYRuntime());
                runtime.setVariable(getBlockParameter(), obj);
            }
        }
        
        runtime.setProcedure(subProcedure);
        
        List<String> varNames = new ArrayList<>();
        varNames.addAll(getMandatoryParameters());
        varNames.addAll(getOptionalParameters().keySet());
        int optSize = getOptionalParameters().size();
        
        List<TYObject> overflow = new ArrayList<>();
        
        int paramPos, nameIndex = 0;
        for (paramPos = 0; paramPos < params.length; paramPos++) {
            
            TYObject param = params[paramPos];
            
            if (blockFlag && getBlockParameter() != null && param instanceof TYProcedureObject) {
                
                runtime.setVariable(getBlockParameter(), param);
                
            } else if (varNames.size() > nameIndex) {
                
                runtime.setVariable(varNames.get(nameIndex++), param);
                
            } else if (paramPos == params.length - 1 && overflow.isEmpty() && param instanceof TYArray) {
                
                overflow.addAll(((TYArray) param).getInternalList());
                
            } else if (getOverflowParameter() != null) {
                
                overflow.add(param);
                
            } else {
                
                Errors.throwError("Trinity.Errors.InvalidArgumentNumberError", "Procedure takes " + getMandatoryParameters().size() + " parameter(s).", runtime);
            }
        }
        
        if (varNames.size() - optSize > nameIndex) {
            
            Errors.throwError("Trinity.Errors.InvalidArgumentNumberError", "Procedure takes " + getMandatoryParameters().size() + " parameter(s).", runtime);
        }
        
        if (getOverflowParameter() != null) {
            
            runtime.setVariable(getOverflowParameter(), new TYArray(overflow));
        }
        
        return getProcedureAction().onAction(runtime, thisObj, params);
    }
}
