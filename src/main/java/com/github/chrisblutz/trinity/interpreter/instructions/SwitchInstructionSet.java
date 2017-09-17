package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class SwitchInstructionSet extends InstructionSet {
    
    private Token switchToken;
    private InstructionSet expression;
    private ProcedureAction action;
    private SwitchInstructionSet child = null;
    
    public SwitchInstructionSet(Token switchToken, InstructionSet expression, ProcedureAction action, Location location) {
        
        super(new Instruction[0], location);
        
        this.switchToken = switchToken;
        this.expression = expression;
        this.action = action;
    }
    
    public Token getSwitchToken() {
        
        return switchToken;
    }
    
    public InstructionSet getExpression() {
        
        return expression;
    }
    
    public ProcedureAction getAction() {
        
        return action;
    }
    
    public SwitchInstructionSet getChild() {
        
        return child;
    }
    
    public void setChild(SwitchInstructionSet child) {
        
        this.child = child;
    }
    
    @Override
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        boolean chaining = runtime.isChainingSwitch();
        runtime.setChainingSwitch(false);
        TYRuntime newRuntime = runtime.clone();
        
        TYObject result = TYObject.NONE;
        
        if (getSwitchToken() == Token.SWITCH) {
            
            TYObject expression = getExpression().evaluate(TYObject.NONE, newRuntime);
            newRuntime.setSwitchObj(expression);
            
            if (getChild() != null) {
                
                result = getChild().evaluate(TYObject.NONE, newRuntime);
            }
            
        } else if (getSwitchToken() == Token.CASE) {
            
            TYObject exp = getExpression().evaluate(TYObject.NONE, newRuntime);
            
            if (chaining || TrinityNatives.toBoolean(runtime.getSwitchObj().tyInvoke("==", runtime, null, null, exp))) {
                
                newRuntime.setChainingSwitch(true);
                
                if (getAction() != null) {
                    
                    result = getAction().onAction(newRuntime, null, TYObject.NONE);
                }
                
                if (!newRuntime.isBroken() && !newRuntime.isReturning() && getChild() != null) {
                    
                    result = getChild().evaluate(TYObject.NONE, newRuntime);
                }
                
            } else if (getChild() != null) {
                
                result = getChild().evaluate(TYObject.NONE, newRuntime);
            }
            
        } else if (getSwitchToken() == Token.DEFAULT) {
            
            if (getAction() != null) {
                
                result = getAction().onAction(newRuntime, null, TYObject.NONE);
            }
        }
        
        newRuntime.dispose(runtime);
        
        return result;
    }
}
