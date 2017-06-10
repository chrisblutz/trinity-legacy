package com.github.chrisblutz.trinity.parser.lines;

import java.io.File;
import java.util.*;


/**
 * @author Christopher Lutz
 */
public class LineSet extends ArrayList<Line> {
    
    private String fileName;
    private File fullFile;
    
    private Map<Integer, String> comments = new HashMap<>();
    private Map<Integer, Integer> commentLeading = new HashMap<>();
    
    private boolean collapsed = false;
    
    private Map<Integer, String[]> collapsedComments = new HashMap<>();
    private Map<Integer, Integer> collapsedCommentLeading = new HashMap<>();
    
    public LineSet(String fileName, File fullFile) {
        
        this.fileName = fileName;
        this.fullFile = fullFile;
    }
    
    public LineSet(LineSet lines) {
        
        this.fileName = lines.getFileName();
        this.fullFile = lines.getFullFile();
        lines.copyComments(this);
    }
    
    public String getFileName() {
        
        return fileName;
    }
    
    public File getFullFile() {
        
        return fullFile;
    }
    
    public void addComment(int line, String comment, int leading) {
        
        comments.put(line, comment);
        commentLeading.put(line, leading);
    }
    
    public Integer[] getCommentedLines() {
        
        return comments.keySet().toArray(new Integer[comments.size()]);
    }
    
    public Integer[] getCollapsedCommentedLines() {
        
        return collapsedComments.keySet().toArray(new Integer[collapsedComments.size()]);
    }
    
    public String getComment(int line) {
        
        return comments.get(line);
    }
    
    public String[] getCollapsedComment(int line) {
        
        return collapsedComments.get(line);
    }
    
    public int getCommentLeading(int line) {
        
        return commentLeading.get(line);
    }
    
    public int getCollapsedCommentLeading(int line) {
        
        return collapsedCommentLeading.get(line);
    }
    
    public boolean hasComments() {
        
        return !comments.isEmpty();
    }
    
    public boolean hasCollapsedComments() {
        
        return !collapsedComments.isEmpty();
    }
    
    public boolean hasComment(int line) {
        
        return comments.containsKey(line);
    }
    
    public boolean hasCollapsedComment(int line) {
        
        return collapsedComments.containsKey(line);
    }
    
    public void collapseComments() {
        
        List<String> comment = new ArrayList<>();
        int line = -1, leading = -1;
        
        for (int lineNum : getCommentedLines()) {
            
            if (lineNum == line + 1 && commentLeading.get(lineNum) == leading) {
                
                comment.add(comments.get(lineNum));
                line = lineNum;
                
            } else {
                
                if (!comments.isEmpty()) {
                    
                    collapsedComments.put(line, comment.toArray(new String[comment.size()]));
                    collapsedCommentLeading.put(line, leading);
                    comment.clear();
                }
                
                comment.add(comments.get(lineNum));
                line = lineNum;
                leading = commentLeading.get(lineNum);
            }
        }
        
        if (!comments.isEmpty()) {
            
            collapsedComments.put(line, comment.toArray(new String[comment.size()]));
            collapsedCommentLeading.put(line, leading);
        }
        
        collapsed = true;
    }
    
    public void copyComments(LineSet set) {
        
        set.comments = comments;
        set.commentLeading = commentLeading;
        set.collapsedComments = collapsedComments;
        set.collapsedCommentLeading = collapsedCommentLeading;
        set.collapsed = collapsed;
    }
    
    @Override
    public String toString() {
        
        StringBuilder str = new StringBuilder("LineSet [Filename: " + getFileName() + "]");
        
        for (Line line : this) {
            
            str.append("\n\t").append(line.toString().replace("\t", "\t\t"));
        }
        
        str.append("\n");
        
        if (!hasCollapsedComments()) {
            
            str.append("No comments.");
            
        } else {
            
            str.append("Comments:");
            
            for (int line : getCollapsedCommentedLines()) {
                
                str.append("\n\t").append(line).append(": ").append(Arrays.toString(getCollapsedComment(line))).append(" [").append(getCollapsedCommentLeading(line)).append(" leading]");
            }
        }
        
        return str.toString();
    }
}
