package com.tngtech.jgiven.impl.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class JGivenLogHandler extends Handler {
    private List<LogRecord> logList = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        logList.add(record);
    }

    @Override
    public void flush() {
        logList.clear();
    }

    @Override
    public void close() throws SecurityException {
    }

    public boolean containsLoggingEvent(Predicate<LogRecord> condition) {
        return logList.stream().anyMatch(condition);
    }

    public boolean containsLoggingEvent(String message, Level level) {
        return containsLoggingEvent(logRecord -> logRecord.getMessage().equals(message)
            && logRecord.getLevel().equals(level));
    }
}
