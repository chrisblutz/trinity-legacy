package com.github.chrisblutz.trinity.parser.tokens;

/**
 * @author Christopher Lutz
 */
public enum Token {
    
    // Misc, Whitespace
    UNIDENTIFIED_TOKEN("\0"), WS_SPACE(" "), WS_TAB("\t"),
    
    // Values
    NIL("nil"), TRUE("true"), FALSE("false"), SUPER("super"),
    __FILE__("__FILE__"), __LINE__("__LINE__"),
    
    // Definitions
    IMPORT("import"), MODULE("module"), CLASS("class"), DEF("def"), NATIVE("native"), INSTANCE_VAR("@"), CLASS_VAR("@@"),
    CLASS_EXTENSION("<<"), GLOBAL_VAR("$"),
    
    // Modifiers
    STATIC("static"), SECURE("secure"), SCOPE_MODIFIER("\0"), PUBLIC_SCOPE("public"), PROTECTED_SCOPE("protected"), MODULE_PROTECTED_SCOPE("module-protected"), PRIVATE_SCOPE("private"),
    
    // Operators
    NEGATIVE_OPERATOR("!"), ASSIGNMENT_OPERATOR("="), NIL_ASSIGNMENT_OPERATOR("||="), DOT_OPERATOR("."), EQUAL_TO("=="), NOT_EQUAL_TO("!="), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL_TO("<="),
    PLUS("+"), PLUS_EQUAL("+="), MINUS("-"), MINUS_EQUAL("-="),
    MULTIPLY("*"), MULTIPLY_EQUAL("*="), DIVIDE("/"), DIVIDE_EQUAL("/="), MODULUS("%"), MODULUS_EQUAL("%="),
    AND("&&"), OR("||"),
    
    // Native Checks
    BLOCK_CHECK("block?"),
    
    // Loops
    IF("if"), ELSIF("elsif"), ELSE("else"), WHILE("while"), FOR("for"), SWITCH("switch"), CASE("case"), DEFAULT("default"), BREAK("break"),
    TRY("try"), CATCH("catch"), FINALLY("finally"),
    
    // Other
    RETURN("return"),
    
    // Punctuation
    LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"), LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"), LEFT_CURLY_BRACKET("{"), RIGHT_CURLY_BRACKET("}"),
    COMMA(","), COLON(":"), SEMICOLON(";"), VERTICAL_BAR("|"), BLOCK_PREFIX("&"), DOUBLE_DOT(".."), TRIPLE_DOT("..."), QUESTION_MARK("?"),
    
    // Comments
    SINGLE_LINE_COMMENT("#"),
    
    // Escape Characters
    BACKSLASH("\\"), BACKSLASH_ESCAPE("\\\\", "\\"),
    NEWLINE_ESCAPE("\\n", "\n"), RETURN_ESCAPE("\\r", "\r"),
    DOUBLE_QUOTE_ESCAPE("\\\"", "\""), SINGLE_QUOTE_ESCAPE("\\\'", "\'"),
    BACKSPACE_ESCAPE("\\b", "\b"), TAB_ESCAPE("\\t", "\t"),
    
    // Strings
    NON_TOKEN_STRING("\0"), LITERAL_STRING("\0"),
    NUMERIC_STRING("\0"), ESCAPED_LITERAL_QUOTE("\'"),
    UNESCAPED_LITERAL_QUOTE("\"");
    
    private String readable, literal;
    
    Token(String readable) {
        
        this(readable, readable);
    }
    
    Token(String readable, String literal) {
        
        this.readable = readable;
        this.literal = literal;
    }
    
    public String getReadable() {
        
        return readable;
    }
    
    public String getLiteral() {
        
        return literal;
    }
    
    public static Token getForString(String readable) {
        
        for (Token t : values()) {
            
            if (t.getReadable().contentEquals(readable)) {
                
                return t;
            }
        }
        
        return UNIDENTIFIED_TOKEN;
    }
    
    public static boolean tokenExists(String readable) {
        
        return getForString(readable) != UNIDENTIFIED_TOKEN;
    }
}
