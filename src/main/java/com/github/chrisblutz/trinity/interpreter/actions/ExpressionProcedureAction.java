package com.github.chrisblutz.trinity.interpreter.actions;

import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.threading.TYThread;


/**
 * @author Christopher Lutz
 */
public class ExpressionProcedureAction implements ProcedureAction {
    
    private String errorClass, method;
    private boolean includeStackTrace;
    private InstructionSet[] sets;
    
    public ExpressionProcedureAction(String errorClass, String method, boolean includeStackTrace, InstructionSet[] sets) {
        
        this.errorClass = errorClass;
        this.method = method;
        this.includeStackTrace = includeStackTrace;
        this.sets = sets;
    }
    
    @Override
    public TYObject onAction(TYRuntime runtime, TYObject thisObj, TYObject... params) {
        
        TYObject returnObj = TYObject.NONE;
        
        TYThread current = TYThread.getCurrentThread();
        for (InstructionSet set : sets) {
            
            if (!includeStackTrace) {
                
                current.getTrinityStack().pop();
            }
            
            current.getTrinityStack().add(errorClass, method, set.getLocation().getFileName(), set.getLocation().getLineNumber());
            
            TYObject result = set.evaluate(TYObject.NONE, runtime);
            
            if (includeStackTrace) {
                
                current.getTrinityStack().pop();
            }
            
            if (result != null && !runtime.isReturning()) {
                
                returnObj = result;
                
            } else if (runtime.isReturning()) {
                
                return runtime.getReturnObject();
                
            } else {
                
                return returnObj;
            }
        }
        
        return returnObj;
    }
}
