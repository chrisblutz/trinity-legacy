package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
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
    private String blockParameter = null;
    
    public TYProcedure(ProcedureAction procedureAction) {
        
        this(procedureAction, new ArrayList<>(), new HashMap<>(), null);
    }
    
    public TYProcedure(ProcedureAction procedureAction, List<String> mandatoryParameters, Map<String, ProcedureAction> optionalParameters, String blockParameter) {
        
        this.procedureAction = procedureAction;
        this.mandatoryParameters = mandatoryParameters;
        this.optionalParameters = optionalParameters;
        this.blockParameter = blockParameter;
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
    
    public TYObject call(TYRuntime runtime, TYProcedure subProcedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
        for (String opt : getOptionalParameters().keySet()) {
            
            ProcedureAction action = getOptionalParameters().get(opt);
            runtime.setVariable(opt, action.onAction(runtime, TYObject.NONE));
        }
        
        int mandatoryNum = getMandatoryParameters().size();
        int optNum = getOptionalParameters().size();
        
        ArrayList<String> opts = new ArrayList<>(getOptionalParameters().keySet());
        
        if (params.length >= mandatoryNum) {
            
            int paramPos;
            for (paramPos = 0; paramPos < mandatoryNum; paramPos++) {
                
                if (subProcedure == null && getBlockParameter() != null && params[paramPos] instanceof TYProcedureObject) {
                    
                    runtime.setVariable(getBlockParameter(), params[paramPos]);
                    
                    paramPos--;
                    
                } else {
                    
                    runtime.setVariable(getMandatoryParameters().get(paramPos), params[paramPos]);
                }
            }
            
            for (; paramPos < mandatoryNum + optNum && paramPos < params.length; paramPos++) {
                
                if (params[paramPos] != TYObject.NONE) {
                    
                    if (subProcedure == null && getBlockParameter() != null && params[paramPos] instanceof TYProcedureObject) {
                        
                        runtime.setVariable(getBlockParameter(), params[paramPos]);
                        
                        paramPos--;
                        
                    } else {
                        
                        String param = opts.get(paramPos - mandatoryNum);
                        runtime.setVariable(param, params[paramPos]);
                    }
                }
            }
            
            if (paramPos < params.length && subProcedure == null && getBlockParameter() != null && params[paramPos] instanceof TYProcedureObject) {
                
                runtime.setVariable(getBlockParameter(), params[paramPos]);
            }
            
        } else {
            
            TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "Procedure takes " + getMandatoryParameters().size() + " parameter(s).");
            error.throwError();
        }
        
        if (getBlockParameter() != null) {
            
            TYObject obj;
            
            if (subProcedure != null) {
                
                obj = new TYProcedureObject(subProcedure, procedureRuntime);
                runtime.setVariable(getBlockParameter(), obj);
                
            } else if (!runtime.hasVariable(getBlockParameter())) {
                
                obj = new TYProcedureObject(new TYProcedure((runtime1, thisObj1, params1) -> TYObject.NONE), new TYRuntime());
                runtime.setVariable(getBlockParameter(), obj);
            }
        }
        
        runtime.setProcedure(subProcedure);
        
        return getProcedureAction().onAction(runtime, thisObj, params);
    }
}
