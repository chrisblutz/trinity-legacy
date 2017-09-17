package com.github.chrisblutz.trinity.interpreter.facets;

import com.github.chrisblutz.trinity.interpreter.KeywordExpressions;
import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.helpers.PostConstraintHelper;
import com.github.chrisblutz.trinity.interpreter.helpers.SingleComponentKeywordExpressionHelper;
import com.github.chrisblutz.trinity.interpreter.instructions.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.parser.tokens.Token;


/**
 * @author Christopher Lutz
 */
public class KeywordExpressionFacets {
    
    public static void registerFacets() {
        
        KeywordExpressions.registerKeywordExpression(Token.IF, true, true, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                return new IfInstructionSet(Token.IF, set, next, location);
            }
        });
        KeywordExpressions.registerKeywordExpression(Token.ELSIF, true, false, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                return new IfInstructionSet(Token.ELSIF, set, next, location);
            }
        });
        KeywordExpressions.registerKeywordConstraint(Token.ELSIF, new Token[]{Token.IF, Token.ELSIF}, "Invalid 'elsif' statement placement.", new PostConstraintHelper() {
            
            @Override
            public boolean postConstraint(InstructionSet set, InstructionSet previous) {
                
                if (set instanceof IfInstructionSet && previous instanceof IfInstructionSet) {
                    
                    ((IfInstructionSet) previous).setChild((IfInstructionSet) set);
                    return true;
                    
                } else {
                    
                    return false;
                }
            }
        });
        KeywordExpressions.registerKeywordExpression(Token.ELSE, 0, true, false, null, (sets, next, location) -> new IfInstructionSet(Token.ELSE, null, next, location));
        KeywordExpressions.registerKeywordConstraint(Token.ELSE, new Token[]{Token.IF, Token.ELSIF}, "Invalid 'else' statement placement.", new PostConstraintHelper() {
            
            @Override
            public boolean postConstraint(InstructionSet set, InstructionSet previous) {
                
                if (set instanceof IfInstructionSet && previous instanceof IfInstructionSet) {
                    
                    ((IfInstructionSet) previous).setChild((IfInstructionSet) set);
                    return true;
                    
                } else {
                    
                    return false;
                }
            }
        });
        
        KeywordExpressions.registerKeywordExpression(Token.SWITCH, true, true, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                return new SwitchInstructionSet(Token.SWITCH, set, next, location);
            }
        });
        KeywordExpressions.registerKeywordExpression(Token.CASE, true, false, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                return new SwitchInstructionSet(Token.CASE, set, next, location);
            }
        });
        KeywordExpressions.registerKeywordConstraint(Token.CASE, new Token[]{Token.SWITCH, Token.CASE}, "Invalid 'case' statement placement.", new PostConstraintHelper() {
            
            @Override
            public boolean postConstraint(InstructionSet set, InstructionSet previous) {
                
                if (set instanceof SwitchInstructionSet && previous instanceof SwitchInstructionSet) {
                    
                    ((SwitchInstructionSet) previous).setChild((SwitchInstructionSet) set);
                    return true;
                    
                } else {
                    
                    return false;
                }
            }
        });
        KeywordExpressions.registerKeywordExpression(Token.DEFAULT, 0, true, false, null, (sets, next, location) -> new SwitchInstructionSet(Token.DEFAULT, null, next, location));
        KeywordExpressions.registerKeywordConstraint(Token.DEFAULT, new Token[]{Token.SWITCH, Token.CASE}, "Invalid 'default' statement placement.", new PostConstraintHelper() {
            
            @Override
            public boolean postConstraint(InstructionSet set, InstructionSet previous) {
                
                if (set instanceof SwitchInstructionSet && previous instanceof SwitchInstructionSet) {
                    
                    ((SwitchInstructionSet) previous).setChild((SwitchInstructionSet) set);
                    return true;
                    
                } else {
                    
                    return false;
                }
            }
        });
        
        KeywordExpressions.registerKeywordExpression(Token.WHILE, true, true, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                return new WhileInstructionSet(set, next, location);
            }
        });
        
        KeywordExpressions.registerKeywordExpression(Token.FOR, 3, true, true, Token.SEMICOLON, (sets, next, location) -> new ForInstructionSet(sets[0], sets[1], sets[2], next, location));
        
        KeywordExpressions.registerKeywordExpression(Token.RETURN, false, true, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                return new ReturnInstructionSet(set, location);
            }
        });
        
        KeywordExpressions.registerKeywordExpression(Token.TRY, 0, true, true, null, (sets, next, location) -> new TryInstructionSet(next, location));
        KeywordExpressions.registerKeywordExpression(Token.CATCH, true, false, new SingleComponentKeywordExpressionHelper() {
            
            @Override
            public InstructionSet interpret(InstructionSet set, ProcedureAction next, Location location) {
                
                if (set.getInstructions().length == 1 && set.getInstructions()[0] instanceof SingleTokenInstruction) {
                    
                    String name = ((SingleTokenInstruction) set.getInstructions()[0]).getContents();
                    return new CatchInstructionSet(next, name, location);
                    
                } else {
                    
                    Errors.throwSyntaxError(Errors.Classes.SYNTAX_ERROR, "Invalid 'catch' statement error variable.", location.getFileName(), location.getLineNumber());
                    return null;
                }
            }
        });
        KeywordExpressions.registerKeywordConstraint(Token.CATCH, new Token[]{Token.TRY}, "Invalid 'catch' statement placement.", new PostConstraintHelper() {
            
            @Override
            public boolean postConstraint(InstructionSet set, InstructionSet previous) {
                
                if (set instanceof CatchInstructionSet && previous instanceof TryInstructionSet) {
                    
                    ((TryInstructionSet) previous).setCatchSet((CatchInstructionSet) set);
                    ((CatchInstructionSet) set).setTrySet((TryInstructionSet) previous);
                    return true;
                    
                } else {
                    
                    return false;
                }
            }
        });
        KeywordExpressions.registerKeywordExpression(Token.FINALLY, 0, true, false, null, (sets, next, location) -> new FinallyInstructionSet(next, location));
        KeywordExpressions.registerKeywordConstraint(Token.FINALLY, new Token[]{Token.TRY, Token.CATCH}, "Invalid 'finally' statement placement.", new PostConstraintHelper() {
            
            @Override
            public boolean postConstraint(InstructionSet set, InstructionSet previous) {
                
                if (set instanceof FinallyInstructionSet) {
                    
                    FinallyInstructionSet finallySet = (FinallyInstructionSet) set;
                    
                    if (previous instanceof TryInstructionSet) {
                        
                        ((TryInstructionSet) previous).setFinallySet(finallySet);
                        
                    } else if (previous instanceof CatchInstructionSet) {
                        
                        ((CatchInstructionSet) previous).getTrySet().setFinallySet(finallySet);
                    }
                }
                
                return false;
            }
        });
    }
}
