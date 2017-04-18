package com.github.chrisblutz.trinity.lang.procedures;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.TYError;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
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
    private Map<String, TYObject> optionalParameters = new HashMap<>();
    private String blockParameter = null;
    
    public TYProcedure(ProcedureAction procedureAction) {
        
        this(procedureAction, new ArrayList<>(), new HashMap<>(), null);
    }
    
    public TYProcedure(ProcedureAction procedureAction, List<String> mandatoryParameters, Map<String, TYObject> optionalParameters, String blockParameter) {
        
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
    
    public Map<String, TYObject> getOptionalParameters() {
        
        return optionalParameters;
    }
    
    public String getBlockParameter() {
        
        return blockParameter;
    }
    
    public TYObject call(TYRuntime runtime, TYStackTrace stackTrace, TYProcedure subProcedure, TYRuntime procedureRuntime, TYObject thisObj, TYObject... params) {
        
        for (String opt : getOptionalParameters().keySet()) {
            
            runtime.setVariable(opt, getOptionalParameters().get(opt));
        }
        
        int mandatoryNum = getMandatoryParameters().size();
        int optNum = getOptionalParameters().size();
        
        if (params.length >= getMandatoryParameters().size()) {
            
            int paramPos;
            for (paramPos = 0; paramPos < mandatoryNum; paramPos++) {
                
                runtime.setVariable(getMandatoryParameters().get(paramPos), params[paramPos]);
            }
            
            for (; paramPos < mandatoryNum + optNum && paramPos < params.length; paramPos++) {
                
                if (params[paramPos] != TYObject.NONE) {
                    
                    String param = new ArrayList<>(getOptionalParameters().keySet()).get(paramPos - mandatoryNum);
                    runtime.setVariable(param, params[paramPos]);
                }
            }
            
        } else {
            
            TYError error = new TYError("Trinity.Errors.InvalidArgumentNumberError", "Procedure takes " + getMandatoryParameters().size() + " parameter(s).", stackTrace);
            error.throwError();
        }
        
        if (getBlockParameter() != null) {
            
            TYObject obj;
            
            if (subProcedure != null) {
                
                obj = new TYProcedureObject(subProcedure, procedureRuntime);
                
            } else {
                
                obj = new TYProcedureObject(new TYProcedure((runtime1, stackTrace1, thisObj1, params1) -> TYObject.NONE), new TYRuntime());
            }
            
            runtime.setVariable(getBlockParameter(), obj);
        }
        
        runtime.setProcedure(subProcedure);
        
        return getProcedureAction().onAction(runtime, stackTrace, thisObj, params);
    }
}
