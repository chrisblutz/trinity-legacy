package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.interpreter.facets.DeclarationFacets;
import com.github.chrisblutz.trinity.interpreter.facets.KeywordExpressionFacets;
import com.github.chrisblutz.trinity.interpreter.facets.KeywordFacets;
import com.github.chrisblutz.trinity.interpreter.facets.OperatorFacets;
import com.github.chrisblutz.trinity.lang.TYObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.TYRuntime;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Christopher Lutz
 */
public class TrinityInterpreter {
    
    private static List<String> importedModules = new ArrayList<>();
    private static List<ProcedureAction> initializationActions = new ArrayList<>();
    
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
    
    public static void addInitializationAction(ProcedureAction action) {
        
        initializationActions.add(action);
    }
    
    public static void runInitializationActions() {
        
        TYRuntime runtime = new TYRuntime();
        
        for (ProcedureAction action : initializationActions) {
            
            action.onAction(runtime, TYObject.NONE);
        }
        
        initializationActions.clear();
    }
    
    public static void interpret(Block block, InterpretEnvironment env) {
        
        for (int i = 0; i < block.size(); i++) {
            
            BlockItem item = block.get(i);
            
            if (item instanceof Block) {
                
                interpret((Block) item, env);
                
            } else if (item instanceof BlockLine) {
                
                Line line = ((BlockLine) item).getLine();
                Block nextBlock = null;
                
                if (i + 1 < block.size() && block.get(i + 1) instanceof Block) {
                    
                    nextBlock = (Block) block.get(++i);
                }
                
                if (line.size() > 0) {
                    
                    Token decl = line.get(0).getToken();
                    
                    if (Declarations.getTokens().contains(decl)) {
                        
                        if (Declarations.checkSize(decl, line)) {
                            
                            Declarations.getDeclaration(decl).define(line, nextBlock, env, new Location(block.getFileName(), block.getFullFile(), line.getLineNumber()));
                            
                        } else {
                            
                            Errors.throwSyntaxError("Trinity.Errors.SyntaxError", "Malformed declaration.", block.getFileName(), line.getLineNumber());
                        }
                        
                    } else {
                        
                        Errors.throwSyntaxError("Trinity.Errors.SyntaxError", "Unrecognized declaration.", block.getFileName(), line.getLineNumber());
                    }
                }
            }
        }
    }
    
    static {
        
        DeclarationFacets.registerFacets();
        OperatorFacets.registerFacets();
        KeywordFacets.registerFacets();
        KeywordExpressionFacets.registerFacets();
    }
}
