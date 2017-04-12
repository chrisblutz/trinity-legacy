package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.defaults.ClassInterpreter;
import com.github.chrisblutz.trinity.interpreter.defaults.ImportInterpreter;
import com.github.chrisblutz.trinity.interpreter.defaults.MethodInterpreter;
import com.github.chrisblutz.trinity.interpreter.defaults.ModuleInterpreter;
import com.github.chrisblutz.trinity.parser.blocks.Block;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TrinityInterpreter {
    
    private static List<DeclarationInterpreter> declarationInterpreters = new ArrayList<>();
    
    public static void registerDeclarationInterpreter(DeclarationInterpreter declarationInterpreter) {
        
        declarationInterpreters.add(declarationInterpreter);
    }
    
    private static List<String> importedModules = new ArrayList<>();
    
    public static void interpret(Block block) {
        
        importedModules.clear();
        
        interpret(block, new InterpretEnvironment());
    }
    
    public static void importModule(String module) {
        
        importedModules.add(module);
    }
    
    public static String[] getImportedModules() {
        
        return importedModules.toArray(new String[importedModules.size()]);
    }
    
    public static void interpret(Block block, InterpretEnvironment env) {
        
        for (DeclarationInterpreter interpreter : declarationInterpreters) {
            
            interpreter.interpret(block, env);
        }
    }
    
    static {
        
        registerDeclarationInterpreter(new ImportInterpreter());
        registerDeclarationInterpreter(new ModuleInterpreter());
        registerDeclarationInterpreter(new ClassInterpreter());
        registerDeclarationInterpreter(new MethodInterpreter());
    }
}
