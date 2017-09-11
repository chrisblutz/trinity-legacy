package com.github.chrisblutz.trinity.parser;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.interpreter.TrinityInterpreter;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.parser.blocks.Block;
import com.github.chrisblutz.trinity.parser.blocks.BlockLine;
import com.github.chrisblutz.trinity.parser.blocks.BlockParseResults;
import com.github.chrisblutz.trinity.parser.comments.CommentUtils;
import com.github.chrisblutz.trinity.parser.lines.Line;
import com.github.chrisblutz.trinity.parser.lines.LineSet;
import com.github.chrisblutz.trinity.parser.tokens.Token;
import com.github.chrisblutz.trinity.parser.tokens.TokenInfo;
import com.github.chrisblutz.trinity.plugins.PluginLoader;
import com.github.chrisblutz.trinity.runner.Runner;
import com.github.chrisblutz.trinity.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Christopher Lutz
 */
public class TrinityParser {
    
    public static final String SOURCE_EXTENSION = "ty";
    
    public static void parse(File file) {
        
        if (file.isDirectory()) {
            
            File[] files = file.listFiles();
            
            if (files != null) {
                
                for (File f : files) {
                    
                    parse(f);
                }
            }
            
        } else {
            
            if (FileUtils.getExtension(file).equalsIgnoreCase(SOURCE_EXTENSION)) {
                
                try {
                    
                    FileInputStream inputStream = new FileInputStream(file);
                    parse(inputStream, file.getName(), file);
                    inputStream.close();
                    
                } catch (Exception e) {
                    
                    System.err.println("An error occurred while parsing '" + file.getName() + "'.");
                    
                    if (CLI.isDebuggingEnabled()) {
                        
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static void parse(InputStream stream, String sourceFile, File sourceLocation) {
        
        Block block = parseContents(stream, sourceFile, sourceLocation);
        TrinityInterpreter.interpret(block);
    }
    
    public static Block parseContents(InputStream stream, String sourceFile, File sourceLocation) {
        
        List<String> lines = new ArrayList<>();
        
        Scanner sc = new Scanner(stream);
        
        while (sc.hasNextLine()) {
            
            lines.add(sc.nextLine());
        }
        
        sc.close();
        
        return parse(sourceFile, sourceLocation, lines.toArray(new String[lines.size()]));
    }
    
    public static Block parseStrings(File container, String[] lines) {
        
        return parse(container.getName(), container, lines);
    }
    
    private static Block parse(String filename, File fullFile, String[] lines) {
        
        PluginLoader.triggerOnFileLoad(filename, fullFile);
        
        // Parse lines into LineSet
        LineSet lineSet = parseFirstLevel(filename, fullFile, lines);
        lineSet = parseComments(lineSet);
        lineSet = parseOutEscapeCharacters(lineSet);
        lineSet = parseOutTokenStrings(lineSet);
        lineSet = parseLiteralStrings(lineSet);
        lineSet = parseOutSpaces(lineSet);
        lineSet = parseOutLeadingWhitespace(lineSet);
        lineSet = stripComments(lineSet);
        lineSet = parseOutEmptyLines(lineSet);
        lineSet = parseNumbers(lineSet);
        lineSet = parseScopes(lineSet);
        lineSet.collapseComments();
        
        // Parse LineSet into Block
        
        return parseToBlock(lineSet);
    }
    
    private static LineSet parseFirstLevel(String filename, File fullFile, String[] lines) {
        
        String litStr = "", token = "";
        int lineNumber = 1;
        
        LineSet set = new LineSet(filename, fullFile);
        
        for (String l : lines) {
            
            Runner.updateLocation(filename, lineNumber);
            
            Line line = new Line(lineNumber++);
            
            for (char c : l.toCharArray()) {
                
                String tS = Character.toString(c);
                
                if (Token.tokenExists(tS)) {
                    
                    if (!litStr.isEmpty()) {
                        
                        if (Token.tokenExists(litStr)) {
                            
                            line.add(new TokenInfo(Token.getForString(litStr), litStr));
                            litStr = "";
                            
                        } else {
                            
                            line.add(new TokenInfo(Token.NON_TOKEN_STRING, litStr));
                            litStr = "";
                        }
                    }
                    
                    if (!token.isEmpty() && Token.tokenExists(token)) {
                        
                        if (Token.tokenExists(token + tS)) {
                            
                            token += tS;
                            
                        } else {
                            
                            line.add(new TokenInfo(Token.getForString(token), token));
                            token = tS;
                        }
                        
                    } else {
                        
                        token += tS;
                    }
                    
                } else {
                    
                    if (!token.isEmpty()) {
                        
                        line.add(new TokenInfo(Token.getForString(token), token));
                        token = "";
                    }
                    
                    litStr += tS;
                }
            }
            
            if (!token.isEmpty() && Token.tokenExists(token)) {
                
                line.add(new TokenInfo(Token.getForString(token), token));
                token = "";
                
            } else if (!litStr.isEmpty()) {
                
                if (Token.tokenExists(litStr)) {
                    
                    line.add(new TokenInfo(Token.getForString(litStr), litStr));
                    
                } else {
                    
                    line.add(new TokenInfo(Token.NON_TOKEN_STRING, litStr));
                }
                
                litStr = "";
            }
            
            set.add(line);
        }
        
        return set;
    }
    
    private static LineSet parseComments(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            
            boolean comment = false;
            StringBuilder builder = new StringBuilder();
            
            for (TokenInfo info : line) {
                
                if (!comment && info.getToken() == Token.SINGLE_LINE_COMMENT) {
                    
                    comment = true;
                    
                } else if (comment) {
                    
                    builder.append(getAppendableString(info, false, true));
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            if (comment) {
                
                newLine.add(new TokenInfo(Token.SINGLE_LINE_COMMENT, builder.toString()));
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseOutEmptyLines(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            if (!line.isEmpty()) {
                
                set.add(line);
            }
        }
        
        return set;
    }
    
    private static LineSet parseOutEscapeCharacters(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            
            for (int i = 0; i < line.size(); i++) {
                
                TokenInfo info = line.get(i);
                
                if (info.getToken() == Token.BACKSLASH) {
                    
                    if (i + 1 < line.size() && line.get(i + 1).getToken() == Token.NON_TOKEN_STRING) {
                        
                        TokenInfo next = line.get(i + 1);
                        i++;
                        String contents = next.getContents();
                        
                        char c = contents.charAt(0);
                        
                        if (Token.tokenExists("\\" + c)) {
                            
                            Token t = Token.getForString("\\" + c);
                            
                            newLine.add(new TokenInfo(t, t.getLiteral()));
                            
                            contents = contents.substring(1);
                            
                            newLine.add(new TokenInfo(Token.NON_TOKEN_STRING, contents));
                            
                        } else if (c == 'u') {
                            
                            contents = contents.substring(1);
                            
                            Matcher m = Pattern.compile("([a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9]).*").matcher(contents);
                            
                            if (m.matches()) {
                                
                                String unicodeValue = m.group(1);
                                contents = contents.substring(4);
                                
                                newLine.add(new TokenInfo(Token.NON_TOKEN_STRING, Character.toString((char) Integer.parseInt(unicodeValue, 16))));
                                
                                newLine.add(new TokenInfo(Token.NON_TOKEN_STRING, contents));
                            }
                            
                        } else {
                            
                            newLine.add(info);
                            newLine.add(next);
                        }
                        
                    } else {
                        
                        newLine.add(info);
                    }
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseOutTokenStrings(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            
            for (int i = 0; i < line.size(); i++) {
                
                TokenInfo info = line.get(i);
                
                if (info.getToken() == Token.NON_TOKEN_STRING && info.getContents().contentEquals(Token.__FILE__.getReadable())) {
                    
                    newLine.add(new TokenInfo(Token.__FILE__, Token.__FILE__.getReadable()));
                    
                } else if (info.getToken() == Token.NON_TOKEN_STRING && info.getContents().contentEquals(Token.__LINE__.getReadable())) {
                    
                    newLine.add(new TokenInfo(Token.__LINE__, Token.__LINE__.getReadable()));
                    
                } else if (info.getToken() == Token.NON_TOKEN_STRING && info.getContents().contentEquals("block") && i < line.size() - 1 && line.get(i + 1).getToken() == Token.QUESTION_MARK) {
                    
                    newLine.add(new TokenInfo(Token.BLOCK_CHECK, Token.BLOCK_CHECK.getReadable()));
                    i++;
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseLiteralStrings(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            
            boolean inEscaped = false;
            boolean inUnescaped = false;
            StringBuilder current = new StringBuilder();
            
            for (TokenInfo info : line) {
                
                if (!inEscaped && !inUnescaped) {
                    
                    if (info.getToken() == Token.ESCAPED_LITERAL_QUOTE) {
                        
                        inEscaped = true;
                        
                    } else if (info.getToken() == Token.UNESCAPED_LITERAL_QUOTE) {
                        
                        inUnescaped = true;
                        
                    } else {
                        
                        newLine.add(info);
                    }
                    
                } else if (inEscaped) {
                    
                    if (info.getToken() == Token.ESCAPED_LITERAL_QUOTE) {
                        
                        inEscaped = false;
                        newLine.add(new TokenInfo(Token.LITERAL_STRING, current.toString()));
                        current = new StringBuilder();
                        
                    } else {
                        
                        current.append(getAppendableString(info, true));
                    }
                    
                } else {
                    
                    if (info.getToken() == Token.UNESCAPED_LITERAL_QUOTE) {
                        
                        inUnescaped = false;
                        newLine.add(new TokenInfo(Token.LITERAL_STRING, current.toString()));
                        current = new StringBuilder();
                        
                    } else {
                        
                        current.append(getAppendableString(info, false));
                    }
                }
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseOutSpaces(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            
            boolean encounteredFirst = false;
            
            for (TokenInfo info : line) {
                
                if (info.getToken() != Token.WS_SPACE && info.getToken() != Token.WS_TAB) {
                    
                    encounteredFirst = true;
                }
                
                if (!encounteredFirst) {
                    
                    newLine.add(info);
                    
                } else if (info.getToken() != Token.WS_SPACE && info.getToken() != Token.WS_TAB) {
                    
                    newLine.add(info);
                }
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseOutLeadingWhitespace(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            
            boolean adding = true;
            int spaces = 0;
            
            for (TokenInfo info : line) {
                
                if (adding) {
                    
                    if (info.getToken() == Token.WS_TAB) {
                        
                        Errors.throwSyntaxError("Trinity.Errors.SyntaxError", "No tabs allowed in leading whitespace.", lines.getFileName(), line.getLineNumber());
                        
                    } else if (info.getToken() == Token.WS_SPACE) {
                        
                        spaces++;
                        
                    } else {
                        
                        adding = false;
                        newLine.add(info);
                    }
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            newLine.setSpaces(spaces);
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet stripComments(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            newLine.setSpaces(line.getSpaces());
            
            for (TokenInfo info : line) {
                
                if (info.getToken() == Token.SINGLE_LINE_COMMENT) {
                    
                    String comment = CommentUtils.stripCommentSymbol(info.getContents());
                    set.addComment(line.getLineNumber(), comment, line.getSpaces());
                    break;
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseNumbers(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            newLine.setSpaces(line.getSpaces());
            
            StringBuilder number = new StringBuilder();
            
            for (int i = 0; i < line.size(); i++) {
                
                TokenInfo info = line.get(i);
                
                if (number.length() > 0) {
                    
                    if (!number.toString().endsWith(".") && !number.toString().endsWith("f") && !number.toString().endsWith("l") && info.getToken() == Token.DOT_OPERATOR) {
                        
                        number.append(info.getContents());
                        
                    } else if (number.toString().endsWith(".") && info.getToken() == Token.NON_TOKEN_STRING && info.getContents().matches("[0-9]+")) {
                        
                        number.append(info.getContents());
                        newLine.add(new TokenInfo(Token.NUMERIC_STRING, number.toString()));
                        number = new StringBuilder();
                        
                    } else {
                        
                        if (number.toString().endsWith(".")) {
                            
                            newLine.add(new TokenInfo(Token.NUMERIC_STRING, number.substring(0, number.length() - 1)));
                            newLine.add(new TokenInfo(Token.DOT_OPERATOR, "."));
                            
                        } else {
                            
                            newLine.add(new TokenInfo(Token.NUMERIC_STRING, number.toString()));
                        }
                        
                        number = new StringBuilder();
                        
                        newLine.add(info);
                    }
                    
                } else if (info.getToken() == Token.NON_TOKEN_STRING && info.getContents().matches("[0-9]+[fFlL]?")) {
                    
                    number = new StringBuilder(info.getContents());
                    
                } else if (info.getToken() == Token.DOT_OPERATOR && i + 1 < line.size() && line.get(i + 1).getToken() == Token.NON_TOKEN_STRING && line.get(i + 1).getContents().matches("[0-9]+[fFlL]?")) {
                    
                    number = new StringBuilder("." + line.get(++i).getContents());
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            if (number.length() > 0) {
                
                if (number.toString().endsWith(".")) {
                    
                    newLine.add(new TokenInfo(Token.NUMERIC_STRING, number.substring(0, number.length() - 1)));
                    newLine.add(new TokenInfo(Token.DOT_OPERATOR, "."));
                    
                } else {
                    
                    newLine.add(new TokenInfo(Token.NUMERIC_STRING, number.toString()));
                }
            }
            
            set.add(newLine);
        }
        
        return set;
    }
    
    private static LineSet parseScopes(LineSet lines) {
        
        LineSet set = new LineSet(lines);
        
        for (Line line : lines) {
            
            Runner.updateLocation(set.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            newLine.setSpaces(line.getSpaces());
            
            for (int i = 0; i < line.size(); i++) {
                
                TokenInfo info = line.get(i);
                
                if (info.getToken() == Token.MODULE && i < line.size() - 2 && line.get(i + 1).getToken() == Token.MINUS && line.get(i + 2).getToken() == Token.PROTECTED_SCOPE) {
                    
                    i += 2;
                    newLine.add(new TokenInfo(Token.MODULE_PROTECTED_SCOPE, Token.MODULE_PROTECTED_SCOPE.getReadable()));
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            set.add(newLine);
        }
        
        LineSet finalSet = new LineSet(set);
        
        for (Line line : set) {
            
            Runner.updateLocation(finalSet.getFileName(), line.getLineNumber());
            
            Line newLine = new Line(line.getLineNumber());
            newLine.setSpaces(line.getSpaces());
            
            for (TokenInfo info : line) {
                
                if (info.getToken() == Token.PRIVATE_SCOPE || info.getToken() == Token.PROTECTED_SCOPE || info.getToken() == Token.MODULE_PROTECTED_SCOPE || info.getToken() == Token.PUBLIC_SCOPE) {
                    
                    newLine.add(new TokenInfo(Token.SCOPE_MODIFIER, info.getContents()));
                    
                } else {
                    
                    newLine.add(info);
                }
            }
            
            finalSet.add(newLine);
        }
        
        return finalSet;
    }
    
    private static Block parseToBlock(LineSet lines) {
        
        return parseBlock(0, lines, 0).getBlock();
    }
    
    private static BlockParseResults parseBlock(int indentLevel, LineSet lines, int start) {
        
        Block level = new Block(lines.getFileName(), lines.getFullFile(), indentLevel);
        
        for (int i = start; i < lines.size(); i++) {
            
            Line l = lines.get(i);
            
            Runner.updateLocation(lines.getFileName(), l.getLineNumber());
            
            int commentLine = l.getLineNumber() - 1;
            if (lines.hasCollapsedComment(commentLine) && lines.getCollapsedCommentLeading(commentLine) == l.getSpaces()) {
                
                l.setLeadingComments(lines.getCollapsedComment(commentLine));
            }
            
            if (l.getSpaces() > level.getSpaces()) {
                
                BlockParseResults results = parseBlock(l.getSpaces(), lines, i);
                level.add(results.getBlock());
                i = results.getLineNumber() - 1;
                
            } else if (l.getSpaces() < level.getSpaces()) {
                
                return new BlockParseResults(level, i);
                
            } else {
                
                level.add(new BlockLine(lines.getFileName(), l));
            }
        }
        
        return new BlockParseResults(level, lines.size());
    }
    
    public static String getAppendableString(TokenInfo info, boolean escape) {
        
        return getAppendableString(info, escape, false);
    }
    
    public static String getAppendableString(TokenInfo info, boolean escape, boolean alwaysReadable) {
        
        if (info.getToken() == Token.NON_TOKEN_STRING) {
            
            return info.getContents();
            
        } else if (alwaysReadable || (escape && !(info.getToken() == Token.BACKSLASH_ESCAPE || info.getToken() == Token.SINGLE_QUOTE_ESCAPE))) {
            
            return info.getToken().getReadable();
            
        } else {
            
            return info.getToken().getLiteral();
        }
    }
}
