package com.github.chrisblutz.trinity.interpreter.instructionsets;

import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.stacktrace.TYStackTrace;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;

import java.io.File;


/**
 * @author Christopher Lutz
 */
public abstract class ObjectEvaluator {
    
    private String fileName;
    private File fullFile;
    private int lineNumber;
    private TYProcedure procedure;
    
    public ObjectEvaluator(String fileName, File fullFile, int lineNumber) {
        
        this.fileName = fileName;
        this.fullFile = fullFile;
        this.lineNumber = lineNumber;
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public File getFullFile() {
        
        return fullFile;
    }
    
    public int getLineNumber() {
        
        return lineNumber;
    }
    
    public TYProcedure getProcedure() {
        
        return procedure;
    }
    
    public void setProcedure(TYProcedure procedure) {
        
        this.procedure = procedure;
    }
    
    public abstract TYObject evaluate(TYObject thisObj, TYRuntime runtime, TYStackTrace stackTrace);
    
    public abstract String toString(String indent);
}
