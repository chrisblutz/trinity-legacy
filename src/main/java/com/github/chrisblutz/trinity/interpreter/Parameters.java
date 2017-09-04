package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;

import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public class Parameters {
    
    private List<String> mandatory;
    private Map<String, ProcedureAction> optional;
    private String block, overflow;
    
    public Parameters(List<String> mandatory, Map<String, ProcedureAction> optional, String block, String overflow) {
        
        this.mandatory = mandatory;
        this.optional = optional;
        this.block = block;
        this.overflow = overflow;
    }
    
    public List<String> getMandatoryParameters() {
        
        return mandatory;
    }
    
    public Map<String, ProcedureAction> getOptionalParameters() {
        
        return optional;
    }
    
    public String getBlockParameter() {
        
        return block;
    }
    
    public String getOverflowParameter() {
        
        return overflow;
    }
}
