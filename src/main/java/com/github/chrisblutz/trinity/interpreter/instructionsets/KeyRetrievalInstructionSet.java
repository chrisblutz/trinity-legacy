package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class KeyRetrievalInstructionSet extends InstructionSet {
    
    public KeyRetrievalInstructionSet(TokenInfo[] tokens, String fileName, File fullFile, int lineNumber) {
        
        super(tokens, fileName, fullFile, lineNumber);
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace) {
        
        if (getTokens().length >= 1) {
            
            if (getTokens()[0].getToken() == Token.NON_TOKEN_STRING) {
                
                String tokenContents = getTokens()[0].getContents();
                TYObject keyObject = null;
                
                if (thisObj == TYObject.NONE) {
                    
                    if (runtime.hasVariable(tokenContents)) {
                        
                        keyObject = runtime.getVariable(tokenContents);
                    }
                }
                
                if (keyObject != null) {
                    
                    List<TYObject> params = new ArrayList<>();
                    
                    for (ChainedInstructionSet set : getChildren()) {
                        
                        TYObject obj = set.evaluate(TYObject.NONE, runtime, stackTrace);
                        
                        if (obj != TYObject.NONE) {
                            
                            params.add(obj);
                        }
                    }
                    
                    return keyObject.tyInvoke("[]", runtime, stackTrace, params.toArray(new TYObject[params.size()]));
                }
            }
            
        } else if (getTokens().length == 0 && thisObj != TYObject.NONE) {
            
            List<TYObject> params = new ArrayList<>();
            
            for (ChainedInstructionSet set : getChildren()) {
                
                TYObject obj = set.evaluate(TYObject.NONE, runtime, stackTrace);
                
                if (obj != TYObject.NONE) {
                    
                    params.add(obj);
                }
            }
            
            return thisObj.tyInvoke("[]", runtime, stackTrace, params.toArray(new TYObject[params.size()]));
        }
        
        return TYBoolean.NONE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "ArrayRetrievalInstructionSet [");
        
        for (TokenInfo info : getTokens()) {
            
            str.append(info.getContents());
        }
        
        str.append("]");
        
        for (ObjectEvaluator child : getChildren()) {
            
            str.append("\n").append(indent).append(child.toString(indent + "\t"));
        }
        
        return str.toString();
    }
}
