package com.github.chrisblutz.trinity;

import com.github.chrisblutz.trinity.parser.TrinityParser;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockItem;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;


/**
 * @author Christopher Lutz
 */
public class TrinityTesting {
    
    private static String[] parsingKeywordsLines, parsingScopeModifiersLines, parsingStringsLines, parsingCommentsAndWhitespaceLines;
    
    @BeforeClass
    public static void loadTestFile() {
        
        parsingKeywordsLines = loadIntoLines("/parsing/keywords.ty");
        parsingScopeModifiersLines = loadIntoLines("/parsing/scope-modifiers.ty");
        parsingStringsLines = loadIntoLines("/parsing/strings.ty");
        parsingCommentsAndWhitespaceLines = loadIntoLines("/parsing/comments-and-whitespace.ty");
    }
    
    private static String[] loadIntoLines(String fileName) {
        
        try {
            
            InputStream stream = TrinityTesting.class.getResourceAsStream(fileName);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                
                lines.add(line);
            }
            reader.close();
            
            return lines.toArray(new String[lines.size()]);
            
        } catch (IOException e) {
            
            fail("File '" + fileName + "' could not be read. (" + e.getClass().getName() + ": " + e.getMessage() + ")");
            
            return new String[0];
        }
    }
    
    @Test
    public void testParser() {
        
        Block block = TrinityParser.parseStrings(new File(""), parsingKeywordsLines);
        
        Token[] keywordExpected = new Token[]{Token.STATIC, Token.NATIVE, Token.SECURE, Token.MODULE, Token.CLASS, Token.DEF, Token.VAL, Token.VAR, Token.INIT, Token.IMPORT, Token.IF, Token.ELSIF, Token.ELSE, Token.SWITCH, Token.CASE, Token.DEFAULT, Token.BREAK, Token.RETURN, Token.TRY, Token.CATCH, Token.FINALLY, Token.NIL, Token.TRUE, Token.FALSE, Token.THIS};
        Token[] keywordActual = convertBlockIntoTokenList(block);
        
        assertArrayEquals(keywordExpected, keywordActual);
        
        block = TrinityParser.parseStrings(new File(""), parsingScopeModifiersLines);
        
        TokenInfo[] scopeModifiersExpected = new TokenInfo[]{new TokenInfo(Token.SCOPE_MODIFIER, "public"), new TokenInfo(Token.SCOPE_MODIFIER, "module-protected"), new TokenInfo(Token.SCOPE_MODIFIER, "protected"), new TokenInfo(Token.SCOPE_MODIFIER, "private")};
        TokenInfo[] scopeModifiersActual = convertBlockIntoTokenInfoList(block);
        
        assertArrayEquals(scopeModifiersExpected, scopeModifiersActual);
        
        block = TrinityParser.parseStrings(new File(""), parsingStringsLines);
        
        TokenInfo[] stringsExpected = new TokenInfo[]{new TokenInfo(Token.NON_TOKEN_STRING, "NonToken"), new TokenInfo(Token.LITERAL_STRING, "UnescapedLiteral\n"), new TokenInfo(Token.LITERAL_STRING, "EscapedLiteral\\n"), new TokenInfo(Token.NUMERIC_STRING, "10.01"), new TokenInfo(Token.NUMERIC_STRING, "10"), new TokenInfo(Token.NUMERIC_STRING, "10l"), new TokenInfo(Token.NUMERIC_STRING, "10f")};
        TokenInfo[] stringsActual = convertBlockIntoTokenInfoList(block);
        
        assertArrayEquals(stringsExpected, stringsActual);
        
        block = TrinityParser.parseStrings(new File(""), parsingCommentsAndWhitespaceLines);
        
        TokenInfo[] commentsAndWhitespaceExpected = new TokenInfo[]{new TokenInfo(Token.LITERAL_STRING, ""), new TokenInfo(Token.LITERAL_STRING, ""), new TokenInfo(Token.NIL, "nil"), new TokenInfo(Token.NUMERIC_STRING, "10")};
        TokenInfo[] commentsAndWhitespaceActual = convertBlockIntoTokenInfoList(block);
        
        assertArrayEquals(commentsAndWhitespaceExpected, commentsAndWhitespaceActual);
        
        // Check leading comments
        Line line = ((BlockLine) block.get(0)).getLine();
        String[] expectedComments = new String[]{"Comment 1", "Comment 1.5"};
        assertArrayEquals(expectedComments, line.getLeadingComments());
    }
    
    private TokenInfo[] convertBlockIntoTokenInfoList(Block block) {
        
        List<TokenInfo> tokens = new ArrayList<>();
        
        for (BlockItem item : block) {
            
            if (item instanceof BlockLine) {
                
                Line line = ((BlockLine) item).getLine();
                
                tokens.addAll(line);
            }
        }
        
        return tokens.toArray(new TokenInfo[tokens.size()]);
    }
    
    private Token[] convertBlockIntoTokenList(Block block) {
        
        TokenInfo[] infos = convertBlockIntoTokenInfoList(block);
        Token[] tokens = new Token[infos.length];
        
        for (int i = 0; i < infos.length; i++) {
            
            tokens[i] = infos[i].getToken();
        }
        
        return tokens;
    }
}
