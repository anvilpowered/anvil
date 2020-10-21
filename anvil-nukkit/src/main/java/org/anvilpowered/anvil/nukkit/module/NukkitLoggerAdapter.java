package org.anvilpowered.anvil.nukkit.module;

import cn.nukkit.utils.LogLevel;
import cn.nukkit.utils.Logger;
import com.google.inject.Binder;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.EnvironmentImpl;
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * @author Jacob Allen
 * @since 0.3
 */
public class NukkitLoggerAdapter extends MarkerIgnoringBase implements LocationAwareLogger {

    protected Logger logger;

    public static void bindLogger(Environment environment, Binder binder) {
        if (environment instanceof EnvironmentImpl) {
            Object logger = ((EnvironmentImpl) environment).getLoggerSupplier().get();
            if (!(logger instanceof Logger)) {
                logger = environment.getInjector().getInstance(Logger.class);
                if (!environment.getName().equals(AnvilPluginInfo.id)) {
                    ((Logger) logger).warning("Defaulting to Anvil logger for plugin: "
                        + environment.getName() + " Use Environment.Builder#setPluginLogger(Object).");
                }
            }
            binder.bind(org.slf4j.Logger.class).toInstance(new NukkitLoggerAdapter((Logger) logger));
        }
    }

    private NukkitLoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    private void log(LogLevel level, String message, Throwable t) {
        String color;
        switch (level) {
            case WARNING:
                color = "\u00a7e";
                break;
            case ERROR:
                color = "\u00a7c";
                break;
            default:
                color = "\u00a7r";
                break;
        }

        String message0 = color + message + "\033[0m"; // Always reset the color
        logger.log(level, message0, t);
    }

    @Override
    public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable throwable) {
        // Nukkit doesn't save the log levels as integers
        log(LogLevel.DEFAULT_LEVEL, message, throwable);
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String message) {
        log(LogLevel.NOTICE, message, null);
    }

    @Override
    public void trace(String format, Object arg) {
        FormattingTuple tuple = MessageFormatter.format(format, arg);
        log(LogLevel.NOTICE, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
        log(LogLevel.NOTICE, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void trace(String format, Object... arguments) {
        FormattingTuple tuple = MessageFormatter.format(format, arguments);
        log(LogLevel.NOTICE, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void trace(String message, Throwable throwable) {
        log(LogLevel.NOTICE, message, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message, null);
    }

    @Override
    public void debug(String format, Object arg) {
        FormattingTuple tuple = MessageFormatter.format(format, arg);
        log(LogLevel.DEBUG, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
        log(LogLevel.DEBUG, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void debug(String format, Object... arguments) {
        FormattingTuple tuple = MessageFormatter.format(format, arguments);
        log(LogLevel.DEBUG, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void debug(String message, Throwable throwable) {
        log(LogLevel.DEBUG, message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String message) {
        log(LogLevel.INFO, message, null);
    }

    @Override
    public void info(String format, Object arg) {
        FormattingTuple tuple = MessageFormatter.format(format, arg);
        log(LogLevel.INFO, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
        log(LogLevel.INFO, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void info(String format, Object... arguments) {
        FormattingTuple tuple = MessageFormatter.format(format, arguments);
        log(LogLevel.INFO, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void info(String message, Throwable throwable) {
        log(LogLevel.INFO, message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String message) {
        log(LogLevel.WARNING, message, null);
    }

    @Override
    public void warn(String format, Object arg) {
        FormattingTuple tuple = MessageFormatter.format(format, arg);
        log(LogLevel.WARNING, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void warn(String format, Object... arguments) {
        FormattingTuple tuple = MessageFormatter.format(format, arguments);
        log(LogLevel.WARNING, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
        log(LogLevel.WARNING, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARNING, message, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        log(LogLevel.ERROR, msg, null);
    }

    @Override
    public void error(String format, Object arg) {
        FormattingTuple tuple = MessageFormatter.format(format, arg);
        log(LogLevel.ERROR, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
        log(LogLevel.ERROR, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void error(String format, Object... arguments) {
        FormattingTuple tuple = MessageFormatter.format(format, arguments);
        log(LogLevel.ERROR, tuple.getMessage(), tuple.getThrowable());
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }
}
