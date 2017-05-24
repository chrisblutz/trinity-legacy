package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.defaults.ClassInterpreter;
import com.github.chrisblutz.trinity.interpreter.defaults.ImportInterpreter;
import com.github.chrisblutz.trinity.interpreter.defaults.MethodInterpreter;
import com.github.chrisblutz.trinity.interpreter.defaults.ModuleInterpreter;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.scope.TYRuntime;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TrinityInterpreter {
    
    private static List<DeclarationInterpreter> declarationInterpreters = new ArrayList<>();
    private static List<ProcedureAction> preMainInitializationCode = new ArrayList<>();
    
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
        
        List<ProcedureAction> initializationActions = new ArrayList<>();
        Block current = new Block(block.getFileName(), block.getFullFile(), block.getSpaces());
        
        for (int i = 0; i < block.size(); i++) {
            
            BlockItem item = block.get(i);
            
            if (item instanceof Block) {
                
                interpret(block, env);
                
            } else if (item instanceof BlockLine) {
                
                Line line = ((BlockLine) item).getLine();
                
                Block nextBlock = null;
                
                if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                    
                    nextBlock = (Block) block.get(++i);
                }
                
                boolean claimed = false;
                for (DeclarationInterpreter interpreter : declarationInterpreters) {
                    
                    if (line.size() > 0 && line.get(0).getToken() == interpreter.getTokenIdentifier()) {
                        
                        claimed = true;
                        interpreter.interpret(line, nextBlock, env, block.getFileName(), block.getFullFile());
                    }
                }
                
                if (!claimed) {
                    
                    if (!env.isInitializable() && env.hasElements()) {
                        
                        Errors.throwError("Trinity.Errors.SyntaxError", "Initialization code prohibited here.", block.getFileName(), line.getLineNumber());
                    }
                    
                    current.add(item);
                    if (nextBlock != null) {
                        
                        current.add(nextBlock);
                    }
                    
                } else if (!current.isEmpty()) {
                    
                    initializationActions.add(ExpressionInterpreter.interpret(current, env, env.isInitializable() ? env.getLastClass().getName() : null, null, true));
                    current.clear();
                }
            }
        }
        
        if (!current.isEmpty()) {
            
            initializationActions.add(ExpressionInterpreter.interpret(current, env, env.getLastClass().getName(), null, true));
            current.clear();
        }
        
        // TODO Add to class if classSTack  > 0, otherwise add to init code to run before main()
        if (!initializationActions.isEmpty()) {
            
            if (env.hasElements()) {
                
                env.getLastClass().addInitializationActions(initializationActions);
                
            } else {
                
                preMainInitializationCode.addAll(initializationActions);
            }
        }
    }
    
    public static void runPreMainInitializationCode() {
        
        for (ProcedureAction action : preMainInitializationCode) {
            
            action.onAction(new TYRuntime(), TYObject.NONE);
        }
    }
    
    static {
        
        registerDeclarationInterpreter(new ImportInterpreter());
        registerDeclarationInterpreter(new ModuleInterpreter());
        registerDeclarationInterpreter(new ClassInterpreter());
        registerDeclarationInterpreter(new MethodInterpreter());
    }
}
