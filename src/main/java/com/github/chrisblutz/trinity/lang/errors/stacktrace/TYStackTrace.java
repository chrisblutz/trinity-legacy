package com.github.chrisblutz.trinity.lang.errors.stacktrace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TYStackTrace implements Cloneable {
    
    private List<TYStackTraceElement> stackTrace = new ArrayList<>();
    
    public TYStackTrace() {
        
        stackTrace.add(TYStackTraceElement.getNativeInitStackTraceElement());
    }
    
    private TYStackTrace(TYStackTraceElement[] elements) {
        
        stackTrace.addAll(Arrays.asList(elements));
    }
    
    public void add(String errorClass, String method, String file, int line) {
        
        stackTrace.add(0, new TYStackTraceElement(errorClass, method, file, line));
    }
    
    public void pop() {
        
        stackTrace.remove(0);
    }
    
    public TYStackTraceElement[] getStackTrace() {
        
        return stackTrace.toArray(new TYStackTraceElement[stackTrace.size()]);
    }
    
    @Override
    public TYStackTrace clone() {
        
        try {
            
            TYStackTrace newStackTrace = (TYStackTrace) super.clone();
            List<TYStackTraceElement> newSTE = new ArrayList<>();
            newSTE.addAll(stackTrace);
            newStackTrace.stackTrace = newSTE;
            return newStackTrace;
            
        } catch (Exception e) {
            
            e.printStackTrace();
            return new TYStackTrace(getStackTrace());
        }
    }
}
