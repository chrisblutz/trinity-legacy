package com.github.chrisblutz.trinity.lang.errors.stacktrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TrinityStack {
    
    private List<StackElement> stack = new ArrayList<>();
    
    public TrinityStack(TrinityStack parent) {
        
        if (parent != null) {
            
            Collections.addAll(stack, parent.getStack());
        }
    }
    
    public void add(String errorClass, String method, String file, int line) {
        
        stack.add(0, new StackElement(errorClass, method, file, line));
    }
    
    public void pop() {
        
        stack.remove(0);
    }
    
    public int size() {
        
        return stack.size();
    }
    
    public void popToSize(int size) {
        
        while (size() > size) {
            
            pop();
        }
    }
    
    public StackElement[] getStack() {
        
        return stack.toArray(new StackElement[stack.size()]);
    }
}
