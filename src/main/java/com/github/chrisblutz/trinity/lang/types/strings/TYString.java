package com.github.chrisblutz.trinity.lang.types.strings;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.types.arrays.TYArray;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYString extends TYObject {
    
    private String internalString;
    private TYArray charArray;
    
    public TYString(String internal) {
        
        super(ClassRegistry.getClass("Trinity.String"));
        
        this.internalString = internal;
        
        List<TYObject> chars = new ArrayList<>();
        
        if (internal.length() == 1) {
            
            chars.add(this);
            
        } else {
            
            for (char c : internalString.toCharArray()) {
                
                chars.add(new TYString(Character.toString(c)));
            }
        }
        
        charArray = new TYArray(chars);
    }
    
    public String getInternalString() {
        
        return internalString;
    }
    
    public TYArray getCharacterArray() {
        
        return charArray;
    }
}
