package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYClass;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.lang.types.TYStaticClassObject;
import com.github.chrisblutz.trinity.lang.types.TYStaticModuleObject;
import com.github.chrisblutz.trinity.lang.types.bool.TYBoolean;
import com.github.chrisblutz.trinity.lang.types.numeric.TYFloat;
import com.github.chrisblutz.trinity.lang.types.numeric.TYInt;
import com.github.chrisblutz.trinity.lang.types.numeric.TYLong;
import com.github.chrisblutz.trinity.lang.types.strings.TYString;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class KeywordInstructionSet extends ObjectEvaluator {
    
    private TokenInfo[] tokens;
    private List<ChainedInstructionSet> children = new ArrayList<>();
    
    public KeywordInstructionSet(TokenInfo[] tokens, String fileName, File fullFile, int lineNumber) {
        
        super(fileName, fullFile, lineNumber);
        this.tokens = tokens;
    }
    
    public TokenInfo[] getTokens() {
        
        return tokens;
    }
    
    public void addChild(ChainedInstructionSet set) {
        
        children.add(set);
    }
    
    public List<ChainedInstructionSet> getChildren() {
        
        return children;
    }
    
    public TYObject evaluate(TYObject thisObj, TYRuntime runtime) {
        
        updateLocation();
        
        if (tokens.length >= 1) {
            
            if (tokens[0].getToken() == Token.LITERAL_STRING) {
                
                return new TYString(tokens[0].getContents());
                
            } else if (tokens[0].getToken() == Token.NIL) {
                
                return TYObject.NIL;
                
            } else if (tokens[0].getToken() == Token.NUMERIC_STRING) {
                
                String numString = tokens[0].getContents();
                
                if (numString.matches("[0-9]+[lL]")) {
                    
                    return new TYLong(Long.parseLong(numString.substring(0, numString.length() - 1)));
                    
                } else if (numString.matches("[0-9]*\\.[0-9]+")) {
                    
                    return new TYFloat(Double.parseDouble(numString));
                    
                } else if (numString.matches("[0-9]*\\.?[0-9]+[fF]")) {
                    
                    return new TYFloat(Double.parseDouble(numString.substring(0, numString.length() - 1)));
                    
                } else if (numString.matches("[0-9]+")) {
                    
                    try {
                        
                        return new TYInt(Integer.parseInt(numString));
                        
                    } catch (Exception e) {
                        
                        return new TYLong(Long.parseLong(numString));
                    }
                }
                
            } else if (tokens[0].getToken() == Token.TRUE) {
                
                return TYBoolean.TRUE;
                
            } else if (tokens[0].getToken() == Token.FALSE) {
                
                return TYBoolean.FALSE;
                
            } else if (tokens[0].getToken() == Token.SUPER) {
                
                if (runtime.isStaticScope()) {
                    
                    TYClass thisClass = runtime.getTyClass();
                    return NativeStorage.getStaticClassObject(thisClass.getSuperclass());
                    
                } else {
                    
                    TYObject thisPointer = runtime.getThis();
                    thisPointer.incrementStackLevel();
                    return thisPointer;
                }
                
            } else if (tokens[0].getToken() == Token.__FILE__) {
                
                return new TYString(getFullFile().getAbsolutePath());
                
            } else if (tokens[0].getToken() == Token.__LINE__) {
                
                return new TYInt(getLineNumber());
                
            } else if (tokens[0].getToken() == Token.BLOCK_CHECK) {
                
                return TYBoolean.valueFor(runtime.getProcedure() != null);
                
            } else if (tokens[0].getToken() == Token.BREAK) {
                
                runtime.setBroken(true);
                return null;
                
            } else if (tokens[0].getToken() == Token.CLASS) {
                
                if (thisObj instanceof TYStaticClassObject) {
                    
                    TYClass tyClass = ((TYStaticClassObject) thisObj).getInternalClass();
                    tyClass.runInitializationActions();
                    
                    return NativeStorage.getClassObject(tyClass);
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.SyntaxError", runtime, "Cannot retrieve a class here.");
                }
                
            } else if (tokens[0].getToken() == Token.MODULE) {
                
                if (thisObj instanceof TYStaticModuleObject) {
                    
                    return NativeStorage.getModuleObject(((TYStaticModuleObject) thisObj).getInternalModule());
                    
                } else {
                    
                    Errors.throwError("Trinity.Errors.SyntaxError", runtime, "Cannot retrieve a module here.");
                }
            }
        }
        
        return TYObject.NONE;
    }
    
    @Override
    public String toString() {
        
        return toString("");
    }
    
    @Override
    public String toString(String indent) {
        
        StringBuilder str = new StringBuilder(indent + "KeywordInstructionSet [");
        
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
