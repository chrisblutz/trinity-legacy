package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.lang.TYObject;

import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class ParameterResults {
    
    private List<String> mandatoryParams;
    private Map<String, TYObject> optParams;
    private String blockParam;
    
    public ParameterResults(List<String> mandatoryParams, Map<String, TYObject> optParams, String blockParam) {
        
        this.mandatoryParams = mandatoryParams;
        this.optParams = optParams;
        this.blockParam = blockParam;
    }
    
    public List<String> getMandatoryParameters() {
        
        return mandatoryParams;
    }
    
    public Map<String, TYObject> getOptionalParameters() {
        
        return optParams;
    }
    
    public String getBlockParam() {
    
        return blockParam;
    }
}
