package de.lorberry.arsen.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private static final List<LogEntry> logs = new ArrayList<>();
    private static final List<LogListener> listeners = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    public record LogEntry(Level level, String message, String timestamp) {

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", timestamp, level, message);
        }
    }

    public interface LogListener {
        void onLog(LogEntry entry);
    }

    public static void init() {
        logs.clear();
        info("Logger initialized");
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    private static void log(Level level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        LogEntry entry = new LogEntry(level, message, timestamp);

        logs.add(entry);
        System.out.println(entry);

        for (LogListener listener : listeners) {
            listener.onLog(entry);
        }
    }

    public static void addListener(LogListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(LogListener listener) {
        listeners.remove(listener);
    }

    public static List<LogEntry> getLogs() {
        return new ArrayList<>(logs);
    }

    public static void clear() {
        logs.clear();
    }
}
