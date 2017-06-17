package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;

import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class ParameterResults {
    
    private List<String> mandatoryParams;
    private Map<String, ProcedureAction> optParams;
    private String blockParam, overflowParam;
    
    public ParameterResults(List<String> mandatoryParams, Map<String, ProcedureAction> optParams, String blockParam, String overflowParam) {
        
        this.mandatoryParams = mandatoryParams;
        this.optParams = optParams;
        this.blockParam = blockParam;
        this.overflowParam = overflowParam;
    }
    
    public List<String> getMandatoryParameters() {
        
        return mandatoryParams;
    }
    
    public Map<String, ProcedureAction> getOptionalParameters() {
        
        return optParams;
    }
    
    public String getBlockParam() {
    
        return blockParam;
    }
    
    public String getOverflowParam() {
        
        return overflowParam;
    }
}
