/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.common.util;

import com.google.inject.Inject;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Jacob Allen
 */
public class JavaUtilLoggingAdapter extends MarkerIgnoringBase implements LocationAwareLogger {

    @Inject
    private Logger logger;

    private final String name;

    public JavaUtilLoggingAdapter(String name) {
        this.name = name;
    }

    private void log(Level level, String message, Throwable throwable) {
        String color;
        switch (level.getName()) {
            case "WARNING":
                color = "\033[0;33m"; // Yellow
                break;
            case "SEVERE":
                color = "\033[0;31m"; // Red
                break;
            default:
                color = "";
                break;
        }

        String message0 = color + "[" + name + "] " + message + "\033[0m"; //Always reset the color
        LogRecord record = new LogRecord(level, message0);
        record.setThrown(throwable);
        logger.log(record);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public void trace(String message) {
        if (isTraceEnabled()) {
            log(Level.FINEST, message, null);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg);
            log(Level.FINEST, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
            log(Level.FINEST, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            log(Level.FINEST, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void trace(String message, Throwable throwable) {
        if (isTraceEnabled()) {
            log(Level.FINEST, message, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public void debug(String message) {
        if (isDebugEnabled()) {
            log(Level.FINE, message, null);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg);
            log(Level.FINE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
            log(Level.FINE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            log(Level.FINE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void debug(String message, Throwable throwable) {
        if (isDebugEnabled()) {
            log(Level.FINE, message, throwable);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public void info(String message) {
        if (isInfoEnabled()) {
            log(Level.INFO, message, null);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg);
            log(Level.INFO, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
            log(Level.INFO, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            log(Level.INFO, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void info(String message, Throwable throwable) {
        if (isInfoEnabled()) {
            log(Level.INFO, message, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public void warn(String message) {
        if (isWarnEnabled()) {
            log(Level.WARNING, message, null);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg);
            log(Level.WARNING, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            log(Level.WARNING, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
            log(Level.WARNING, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (isWarnEnabled()) {
            log(Level.WARNING, message, throwable);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void error(String message) {
        if (isErrorEnabled()) {
            log(Level.SEVERE, message, null);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg);
            log(Level.SEVERE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
            log(Level.SEVERE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            log(Level.SEVERE, tuple.getMessage(), tuple.getThrowable());
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (isErrorEnabled()) {
            log(Level.SEVERE, message, throwable);
        }
    }

    @Override
    public void log(Marker marker, String fqcn, int level, String message, Object[] argArray, Throwable throwable) {
        Level level0;
        switch (level) {
            case LocationAwareLogger.TRACE_INT:
                level0 = Level.FINEST;
                break;
            case LocationAwareLogger.DEBUG_INT:
                level0 = Level.FINE;
                break;
            case LocationAwareLogger.INFO_INT:
                level0 = Level.INFO;
                break;
            case LocationAwareLogger.WARN_INT:
                level0 = Level.WARNING;
                break;
            case LocationAwareLogger.ERROR_INT:
                level0 = Level.SEVERE;
                break;
            default:
                throw new IllegalStateException(level + " is not a supported logging level!");
        }
        if (logger.isLoggable(level0)) {
            log(level0, message, throwable);
        }
    }
}
