package com.tngtech.jgiven.impl.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JGivenLogHandler extends Handler {
    static private List<LogRecord> logList = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        logList.add(record);
    }

    @Override
    public void flush() {
        resetEvents();
    }

    @Override
    public void close() throws SecurityException {}

    public static boolean containsLoggingEvent(String message, Level level) {
        return logList.stream().anyMatch(logRecord -> logRecord.getMessage().equals(message)
                                        && logRecord.getLevel().equals(level));
    }
    public static void resetEvents() {
        logList.clear();
    }
}
