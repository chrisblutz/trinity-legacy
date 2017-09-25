package com.github.chrisblutz.trinity.interpreter.facets;

import com.github.chrisblutz.trinity.interpreter.*;
import com.github.chrisblutz.trinity.interpreter.actions.InterfaceMethodProcedureAction;
import com.github.chrisblutz.trinity.interpreter.actions.VariableProcedureAction;
import com.github.chrisblutz.trinity.interpreter.instructions.InstructionSet;
import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TYProcedure;
import com.github.chrisblutz.trinity.natives.TrinityNatives;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;
import com.github.chrisblutz.trinity.plugins.PluginLoader;

import java.util.*;


/**
 * @author Christopher Lutz
 */
public class DeclarationFacets {
    
    public static final ProcedureAction DEFAULT_METHOD = (runtime, thisObj, params) -> TYObject.NONE;
    
    public static void registerFacets() {
        
        // Definition for import statements
        Declarations.register(Token.IMPORT, 2, 0, (line, nextBlock, env, location) -> {
            
            if (line.get(1).getToken() == Token.NON_TOKEN_STRING) {
                
                StringBuilder importName = new StringBuilder();
                
                for (int i = 1; i < line.size(); i++) {
                    
                    if (line.get(i).getToken() == Token.NON_TOKEN_STRING) {
                        
                        importName.append(line.get(i).getContents());
                        
                    } else {
                        
                        importName.append(line.get(i).getToken().getLiteral());
                    }
                }
                
                TrinityInterpreter.importModule(importName.toString());
            }
        });
        
        // Definitions for scope modifiers (public, module-protected, protected, private)
        Declarations.register(Token.SCOPE_MODIFIER, 1, 1, (line, nextBlock, env, location) -> {
            
            TokenInfo info = line.get(0);
            Scope scope = Scope.PUBLIC;
            
            if (info.getContents().contentEquals(Token.PRIVATE_SCOPE.getReadable())) {
                
                scope = Scope.PRIVATE;
                
            } else if (info.getContents().contentEquals(Token.PROTECTED_SCOPE.getReadable())) {
                
                scope = Scope.PROTECTED;
                
            } else if (info.getContents().contentEquals(Token.MODULE_PROTECTED_SCOPE.getReadable())) {
                
                scope = Scope.MODULE_PROTECTED;
                
            } else if (info.getContents().contentEquals(Token.PUBLIC_SCOPE.getReadable())) {
                
                scope = Scope.PUBLIC;
            }
            
            if (nextBlock != null) {
                
                InterpretEnvironment newEnv = env.append(scope);
                TrinityInterpreter.interpret(nextBlock, newEnv);
            }
            
            env.setScope(scope);
        });
        
        // Definition for variable declarations
        Declarations.register(Token.VAR, 2, 0, (line, nextBlock, env, location) -> {
            
            boolean staticVar = false;
            boolean nativeVar = false;
            String name = null;
            int position = 1;
            
            if (line.get(position).getToken() == Token.NATIVE) {
                
                nativeVar = true;
                position++;
            }
            
            if (line.get(position).getToken() == Token.STATIC) {
                
                staticVar = true;
                position++;
            }
            
            if (env.getClassStack().isEmpty()) {
                
                Errors.throwSyntaxError(Errors.Classes.SCOPE_ERROR, "Variables must be declared within a class.", location.getFileName(), location.getLineNumber());
            }
            
            TYClass containerClass = env.getClassStack().get(env.getClassStack().size() - 1);
            
            TokenInfo[] parts = new TokenInfo[line.size() - position];
            System.arraycopy(line.toArray(new TokenInfo[line.size()]), position, parts, 0, parts.length);
            
            List<List<TokenInfo>> splitParts = ExpressionInterpreter.splitTokens(parts, Token.COMMA);
            
            for (int i = 0; i < splitParts.size(); i++) {
                
                List<TokenInfo> tokens = splitParts.get(i);
                
                position = 0;
                TokenInfo nameInfo = tokens.get(position);
                if (nameInfo.getToken() == Token.NON_TOKEN_STRING) {
                    
                    name = nameInfo.getContents();
                    position++;
                }
                
                ProcedureAction action = null;
                if (nativeVar) {
                    
                    action = TrinityNatives.getFieldProcedureAction(containerClass.getName(), name, location.getFileName(), location.getLineNumber());
                    
                } else {
                    
                    if (position < tokens.size() && tokens.get(position).getToken() == Token.ASSIGNMENT_OPERATOR) {
                        
                        List<TokenInfo> assignment = new ArrayList<>();
                        for (int i2 = position + 1; i2 < tokens.size(); i2++) {
                            
                            assignment.add(tokens.get(i2));
                        }
                        
                        String stackName = "<var='" + name + "'>";
                        InstructionSet set = ExpressionInterpreter.interpretExpression(null, assignment.toArray(new TokenInfo[assignment.size()]), location, containerClass.getName(), stackName, i == splitParts.size() - 1 ? nextBlock : null);
                        action = new VariableProcedureAction(stackName, location, containerClass, set);
                    }
                }
                
                if (staticVar) {
                    
                    containerClass.registerClassVariable(name, nativeVar, line.getLeadingComments(), action, env.getScope(), false, TrinityInterpreter.getImportedModules());
                    
                } else {
                    
                    containerClass.registerInstanceVariable(name, nativeVar, line.getLeadingComments(), action, env.getScope(), false, TrinityInterpreter.getImportedModules());
                }
            }
        });
        
        // Definition for constant declarations
        Declarations.register(Token.VAL, 2, 0, (line, nextBlock, env, location) -> {
            
            boolean staticVar = false;
            boolean nativeVar = false;
            String name = null;
            int position = 1;
            
            if (line.get(position).getToken() == Token.NATIVE) {
                
                nativeVar = true;
                position++;
            }
            
            if (line.get(position).getToken() == Token.STATIC) {
                
                staticVar = true;
                position++;
            }
            
            if (env.getClassStack().isEmpty()) {
                
                Errors.throwSyntaxError(Errors.Classes.SCOPE_ERROR, "Constants must be declared within a class.", location.getFileName(), location.getLineNumber());
            }
            
            TYClass containerClass = env.getClassStack().get(env.getClassStack().size() - 1);
            
            TokenInfo[] parts = new TokenInfo[line.size() - position];
            System.arraycopy(line.toArray(new TokenInfo[line.size()]), position, parts, 0, parts.length);
            
            List<List<TokenInfo>> splitParts = ExpressionInterpreter.splitTokens(parts, Token.COMMA);
            
            for (int i = 0; i < splitParts.size(); i++) {
                
                List<TokenInfo> tokens = splitParts.get(i);
                
                position = 0;
                TokenInfo nameInfo = tokens.get(position);
                if (nameInfo.getToken() == Token.NON_TOKEN_STRING) {
                    
                    name = nameInfo.getContents();
                    position++;
                }
                
                ProcedureAction action = null;
                if (nativeVar) {
                    
                    action = TrinityNatives.getFieldProcedureAction(containerClass.getName(), name, location.getFileName(), location.getLineNumber());
                    
                } else {
                    
                    if (position < tokens.size() && tokens.get(position).getToken() == Token.ASSIGNMENT_OPERATOR) {
                        
                        List<TokenInfo> assignment = new ArrayList<>();
                        for (int i2 = position + 1; i2 < tokens.size(); i2++) {
                            
                            assignment.add(tokens.get(i2));
                        }
                        
                        String stackName = "<val='" + name + "'>";
                        InstructionSet set = ExpressionInterpreter.interpretExpression(null, assignment.toArray(new TokenInfo[assignment.size()]), location, containerClass.getName(), stackName, i == splitParts.size() - 1 ? nextBlock : null);
                        action = new VariableProcedureAction(stackName, location, containerClass, set);
                    }
                }
                
                if (staticVar) {
                    
                    containerClass.registerClassVariable(name, nativeVar, line.getLeadingComments(), action, env.getScope(), true, TrinityInterpreter.getImportedModules());
                    
                } else {
                    
                    containerClass.registerInstanceVariable(name, nativeVar, line.getLeadingComments(), action, env.getScope(), true, TrinityInterpreter.getImportedModules());
                }
            }
        });
        
        // Definition for module declarations
        Declarations.register(Token.MODULE, 2, 2, (line, nextBlock, env, location) -> {
            
            if (line.get(1).getToken() == Token.NON_TOKEN_STRING) {
                
                String moduleName = line.get(1).getContents();
                
                if (env.hasElements()) {
                    
                    moduleName = env.getEnvironmentString() + "." + moduleName;
                }
                
                TYModule tyModule = ModuleRegistry.getModule(moduleName);
                tyModule.setLeadingComments(line.getLeadingComments());
                if (!env.getModuleStack().isEmpty()) {
                    
                    TYModule topModule = env.getLastModule();
                    tyModule.setParentModule(topModule);
                }
                InterpretEnvironment newEnv = env.append(tyModule);
                
                if (nextBlock != null) {
                    
                    TrinityInterpreter.interpret(nextBlock, newEnv);
                }
            }
        });
        
        // Definition for class declarations
        Declarations.register(Token.CLASS, 2, 0, (line, nextBlock, env, location) -> {
            
            boolean nativeClass = false;
            String extension = null;
            List<String> interfaces = new ArrayList<>();
            int offset = 1;
            
            if (line.get(offset).getToken() == Token.NATIVE) {
                
                nativeClass = true;
                offset++;
            }
            
            String className = line.get(offset).getContents();
            offset++;
            
            if (line.size() - offset > 1 && line.get(offset).getToken() == Token.CLASS_EXTENSION) {
                
                offset++;
                StringBuilder ext = new StringBuilder();
                for (; offset < line.size(); offset++) {
                    
                    TokenInfo info = line.get(offset);
                    
                    if (info.getToken() == Token.INTERFACE_IMPLEMENTATION) {
                        
                        break;
                        
                    } else if (info.getToken() == Token.NON_TOKEN_STRING) {
                        
                        ext.append(info.getContents());
                        
                    } else {
                        
                        ext.append(info.getToken().getLiteral());
                    }
                }
                
                extension = ext.toString();
            }
            
            if (line.size() - offset > 1 && line.get(offset).getToken() == Token.INTERFACE_IMPLEMENTATION) {
                
                offset++;
                TokenInfo[] names = new TokenInfo[line.size() - offset];
                System.arraycopy(line.toArray(), line.size() - names.length, names, 0, names.length);
                List<List<TokenInfo>> nameTokens = ExpressionInterpreter.splitTokens(names, Token.COMMA);
                for (List<TokenInfo> tokens : nameTokens) {
                    
                    StringBuilder ext = new StringBuilder();
                    for (TokenInfo info : tokens) {
                        
                        if (info.getToken() == Token.NON_TOKEN_STRING) {
                            
                            ext.append(info.getContents());
                            
                        } else {
                            
                            ext.append(info.getToken().getLiteral());
                        }
                    }
                    
                    interfaces.add(ext.toString());
                }
            }
            
            if (env.hasElements()) {
                
                className = env.getEnvironmentString() + "." + className;
            }
            
            if (nativeClass) {
                
                Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Native classes are not currently supported.", location.getFileName(), location.getLineNumber());
                
            } else {
                
                TYClass tyClass = ClassRegistry.getClass(className);
                tyClass.setLeadingComments(line.getLeadingComments());
                if (!env.getModuleStack().isEmpty()) {
                    
                    TYModule topModule = env.getLastModule();
                    tyClass.setModule(topModule);
                }
                if (extension != null) {
                    
                    tyClass.setSuperclassString(extension, TrinityInterpreter.getImportedModules());
                }
                if (interfaces.size() > 0) {
                    
                    tyClass.setSuperinterfaceStrings(interfaces.toArray(new String[interfaces.size()]), TrinityInterpreter.getImportedModules());
                }
                InterpretEnvironment newEnv = env.append(tyClass);
                
                if (nextBlock != null) {
                    
                    TrinityInterpreter.interpret(nextBlock, newEnv);
                }
                
                PluginLoader.triggerOnClassLoadFromFile(location.getFileName(), location.getFile(), tyClass);
            }
        });
        
        // Definition for interface declarations
        Declarations.register(Token.INTERFACE, 2, 0, (line, nextBlock, env, location) -> {
            
            boolean nativeInterface = false;
            List<String> extensions = new ArrayList<>();
            int offset = 1;
            
            if (line.get(offset).getToken() == Token.NATIVE) {
                
                nativeInterface = true;
                offset++;
            }
            
            String className = line.get(offset).getContents();
            offset++;
            
            if (line.size() - offset > 1 && line.get(offset).getToken() == Token.CLASS_EXTENSION) {
                
                offset++;
                TokenInfo[] names = new TokenInfo[line.size() - offset];
                System.arraycopy(line.toArray(), line.size() - names.length, names, 0, names.length);
                List<List<TokenInfo>> nameTokens = ExpressionInterpreter.splitTokens(names, Token.COMMA);
                for (List<TokenInfo> tokens : nameTokens) {
                    
                    StringBuilder ext = new StringBuilder();
                    for (TokenInfo info : tokens) {
                        
                        if (info.getToken() == Token.NON_TOKEN_STRING) {
                            
                            ext.append(info.getContents());
                            
                        } else {
                            
                            ext.append(info.getToken().getLiteral());
                        }
                    }
                    
                    extensions.add(ext.toString());
                }
            }
            
            if (env.hasElements()) {
                
                className = env.getEnvironmentString() + "." + className;
            }
            
            if (nativeInterface) {
                
                Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Native interfaces are not currently supported.", location.getFileName(), location.getLineNumber());
                
            } else {
                
                TYClass tyClass = ClassRegistry.getClass(className);
                tyClass.setInterface(true);
                tyClass.registerMethod(new TYMethod("initialize", false, true, true, tyClass, new TYProcedure((runtime, thisObj, params) -> {
                    
                    Errors.throwError(Errors.Classes.INVALID_TYPE_ERROR, runtime, "Cannot create an instance of an interface.");
                    return TYObject.NONE;
                    
                }, false)));
                tyClass.setLeadingComments(line.getLeadingComments());
                if (!env.getModuleStack().isEmpty()) {
                    
                    TYModule topModule = env.getLastModule();
                    tyClass.setModule(topModule);
                }
                if (extensions.size() > 0) {
                    
                    tyClass.setSuperinterfaceStrings(extensions.toArray(new String[extensions.size()]), TrinityInterpreter.getImportedModules());
                }
                InterpretEnvironment newEnv = env.append(tyClass);
                
                if (nextBlock != null) {
                    
                    TrinityInterpreter.interpret(nextBlock, newEnv);
                }
                
                PluginLoader.triggerOnClassLoadFromFile(location.getFileName(), location.getFile(), tyClass);
            }
        });
        
        // Definition for method declarations
        Declarations.register(Token.DEF, 2, 0, (line, nextBlock, env, location) -> {
            
            boolean staticMethod = false;
            boolean nativeMethod = false;
            boolean secureMethod = false;
            String name;
            int position = 1;
            
            if (line.get(position).getToken() == Token.NATIVE) {
                
                nativeMethod = true;
                position++;
            }
            
            if (line.get(position).getToken() == Token.STATIC) {
                
                staticMethod = true;
                position++;
            }
            
            if (line.get(position).getToken() == Token.SECURE) {
                
                secureMethod = true;
                position++;
            }
            
            TokenInfo nameInfo = line.get(position);
            if (nameInfo.getToken() == Token.NON_TOKEN_STRING) {
                
                name = nameInfo.getContents();
                position++;
                
            } else if (Operators.isOperatorMethod(nameInfo.getToken())) {
                
                name = nameInfo.getContents();
                position++;
                
            } else if (nameInfo.getToken() == Token.LEFT_SQUARE_BRACKET && position + 1 < line.size() && line.get(position + 1).getToken() == Token.RIGHT_SQUARE_BRACKET) {
                
                if (position + 2 < line.size() && line.get(position + 2).getToken() == Token.ASSIGNMENT_OPERATOR) {
                    
                    name = "[]=";
                    position += 3;
                    
                } else {
                    
                    name = "[]";
                    position += 2;
                }
                
            } else {
                
                name = nameInfo.getToken().getReadable();
                position++;
            }
            
            if (env.getClassStack().isEmpty()) {
                
                Errors.throwSyntaxError(Errors.Classes.SCOPE_ERROR, "Methods must be declared within a class.", location.getFileName(), location.getLineNumber());
            }
            
            TYClass containerClass = env.getClassStack().get(env.getClassStack().size() - 1);
            
            List<String> mandatoryParams = new ArrayList<>();
            Map<String, ProcedureAction> optParams = new LinkedHashMap<>();
            String blockParam = null;
            String overflowParam = null;
            
            if (position < line.size() && line.get(position).getToken() == Token.LEFT_PARENTHESIS && line.get(line.size() - 1).getToken() == Token.RIGHT_PARENTHESIS) {
                
                position++;
                TokenInfo[] tokens = Arrays.copyOfRange(line.toArray(new TokenInfo[line.size()]), position, line.size() - 1);
                Parameters params = ExpressionInterpreter.interpretParameters(tokens, location, containerClass.getName(), name);
                mandatoryParams = params.getMandatoryParameters();
                optParams = params.getOptionalParameters();
                blockParam = params.getBlockParameter();
                overflowParam = params.getOverflowParameter();
            }
            
            ProcedureAction action;
            if (nativeMethod) {
                
                action = TrinityNatives.getMethodProcedureAction(containerClass.getName(), name, location.getFileName(), location.getLineNumber());
                
            } else {
                
                if (nextBlock == null) {
                    
                    action = DEFAULT_METHOD;
                    
                } else {
                    
                    action = ExpressionInterpreter.interpret(nextBlock, env, env.getClassStack().get(env.getClassStack().size() - 1).getName(), name, true);
                }
            }
            
            // Check if container is an interface, and if it is, make sure this method has no body
            if (containerClass.isInterface() && action != DEFAULT_METHOD) {
                
                Errors.throwSyntaxError(Errors.Classes.INHERITANCE_ERROR, "Methods within interfaces cannot have a body.", location.getFileName(), location.getLineNumber());
                
            } else if (containerClass.isInterface() && name.equals("initialize")) {
                
                Errors.throwSyntaxError(Errors.Classes.SCOPE_ERROR, "Interfaces cannot declare an 'initialize' method.", location.getFileName(), location.getLineNumber());
                
            } else if (containerClass.isInterface()) {
                
                action = new InterfaceMethodProcedureAction(name);
            }
            
            TYProcedure procedure = new TYProcedure(action, mandatoryParams, optParams, blockParam, overflowParam, true);
            
            TYMethod method = new TYMethod(name, staticMethod, false, secureMethod, containerClass, procedure);
            method.setScope(env.getScope());
            method.setLeadingComments(line.getLeadingComments());
            method.importModules(TrinityInterpreter.getImportedModules());
            containerClass.registerMethod(method);
        });
        
        // Definition for initialization actions
        Declarations.register(Token.INIT, 1, 1, (line, nextBlock, env, location) -> {
            
            ProcedureAction action = ExpressionInterpreter.interpret(nextBlock, env, env.isInitializable() ? env.getLastClass().getName() : null, "<init>", true);
            if (env.hasElements()) {
                
                env.getLastClass().addInitializationAction(action);
                
            } else {
                
                TrinityInterpreter.addInitializationAction(action);
            }
        });
    }
}
