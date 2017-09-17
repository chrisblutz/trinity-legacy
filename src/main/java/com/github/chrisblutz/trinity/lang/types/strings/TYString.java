package com.github.chrisblutz.trinity.lang.types.strings;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYString extends TYObject {
    
    private String internalString;
    private List<TYObject> chars;
    private TYArray charArray = null;
    
    public TYString(String internal) {
        
        super(ClassRegistry.getClass(TrinityNatives.Classes.STRING));
        
        this.internalString = internal;
        
        chars = new ArrayList<>();
        
        if (internal.length() == 1) {
            
            chars.add(this);
            
        } else {
            
            for (char c : internalString.toCharArray()) {
                
                chars.add(new TYString(Character.toString(c)));
            }
        }
    }
    
    public String getInternalString() {
        
        return internalString;
    }
    
    public TYArray getCharacterArray() {
        
        if (charArray == null) {
            
            charArray = new TYArray(chars);
        }
        
        return charArray;
    }
}
