package com.github.chrisblutz.trinity.lang.errors.stacktrace;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TrinityStack {
    
    private static List<StackElement> stack = new ArrayList<>();
    
    static {
        
        stack.add(StackElement.getNativeInitStackElement());
    }
    
    public static void add(String errorClass, String method, String file, int line) {
        
        stack.add(0, new StackElement(errorClass, method, file, line));
    }
    
    public static void pop() {
        
        stack.remove(0);
    }
    
    public static StackElement[] getStack() {
        
        return stack.toArray(new StackElement[stack.size()]);
    }
}
