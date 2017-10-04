package com.github.chrisblutz.trinity.logging;

import com.github.chrisblutz.trinity.cli.CLI;
import com.github.chrisblutz.trinity.info.TrinityInfo;

import java.io.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.*;


/**
 * @author Christopher Lutz
 */
public class TrinityLogging {
    
    private static PrintStream printStream;
    
    private static DateFormat dateFormat;
    private static Formatter formatter;
    private static Logger logger;
    
    public static void setup() {
        
        try {
    
            File logFile = new File("logs/trinity.log");
            logFile.getParentFile().mkdirs();
            printStream = new PrintStream(new FileOutputStream(logFile));
            
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
            formatter = new Formatter() {
                
                @Override
                public String format(LogRecord record) {
                    
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(record.getMillis());
                    String date = dateFormat.format(calendar.getTime());
                    String formatted = date + " " + record.getLevel() + ": " + record.getMessage();
                    if (record.getThrown() != null) {
                        
                        StringWriter writer = new StringWriter();
                        PrintWriter printWriter = new PrintWriter(writer);
                        record.getThrown().printStackTrace(printWriter);
                        printWriter.close();
                        formatted += "\n" + writer.toString();
                    }
                    return formatted;
                }
            };
            Handler handler = new Handler() {
        
                @Override
                public void publish(LogRecord record) {
            
                    printStream.println(formatter.format(record));
                }
        
                @Override
                public void flush() {
            
                    printStream.flush();
                }
        
                @Override
                public void close() throws SecurityException {
            
                    printStream.close();
                }
            };
            
            logger = Logger.getLogger("Trinity");
            logger.setUseParentHandlers(false);
            logger.addHandler(handler);
            
        } catch (IOException e) {
            
            if (CLI.isDebuggingEnabled()) {
                
                System.err.println("Couldn't create log file.");
            }
        }
    }
    
    public static void logInterpreterInfo() {
        
        if (logger.isLoggable(Level.INFO)) {
            
            logger.info("Trinity Interpreter: " + TrinityInfo.getVersionString());
            
            logger.info("Java Version: " + System.getProperty("java.version"));
            logger.info("OS: " + System.getProperty("os.name") + " (v" + System.getProperty("os.version") + ") [" + System.getProperty("os.arch") + "]\n");
        }
    }
    
    public static void info(String message) {
        
        logger.info(message);
    }
}
