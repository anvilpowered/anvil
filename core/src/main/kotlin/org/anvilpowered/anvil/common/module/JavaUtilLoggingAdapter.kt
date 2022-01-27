/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
package org.anvilpowered.anvil.common.module

import com.google.inject.Binder
import com.google.inject.Inject
import org.anvilpowered.anvil.api.environment.Environment
import org.slf4j.Marker
import org.slf4j.helpers.MarkerIgnoringBase
import org.slf4j.helpers.MessageFormatter
import org.slf4j.spi.LocationAwareLogger
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * @author Jacob Allen
 */
class JavaUtilLoggingAdapter(name: String) : MarkerIgnoringBase(), LocationAwareLogger {

    @Inject
    private lateinit var logger: Logger

    private fun log(level: Level, message: String, throwable: Throwable?) {
        val color = when (level.name) {
            "WARNING" -> "\u001b[0;33m" // Yellow
            "SEVERE" -> "\u001b[0;31m" // Red
            else -> ""
        }
        val message0 = "$color$message\u001b[0m" //Always reset the color
        val record = LogRecord(level, message0)
        record.loggerName = name
        record.thrown = throwable
        logger.log(record)
    }

    override fun isTraceEnabled(): Boolean {
        return logger.isLoggable(Level.FINEST)
    }

    override fun trace(message: String) {
        if (isTraceEnabled) {
            log(Level.FINEST, message, null)
        }
    }

    override fun trace(format: String, arg: Any) {
        if (isTraceEnabled) {
            val tuple = MessageFormatter.format(format, arg)
            log(Level.FINEST, tuple.message, tuple.throwable)
        }
    }

    override fun trace(format: String, arg1: Any, arg2: Any) {
        if (isTraceEnabled) {
            val tuple = MessageFormatter.format(format, arg1, arg2)
            log(Level.FINEST, tuple.message, tuple.throwable)
        }
    }

    override fun trace(format: String, vararg arguments: Any) {
        if (isTraceEnabled) {
            val tuple = MessageFormatter.arrayFormat(format, arguments)
            log(Level.FINEST, tuple.message, tuple.throwable)
        }
    }

    override fun trace(message: String, throwable: Throwable) {
        if (isTraceEnabled) {
            log(Level.FINEST, message, throwable)
        }
    }

    override fun isDebugEnabled(): Boolean = logger.isLoggable(Level.FINE)

    override fun debug(message: String) {
        if (isDebugEnabled) {
            log(Level.FINE, message, null)
        }
    }

    override fun debug(format: String, arg: Any) {
        if (isDebugEnabled) {
            val tuple = MessageFormatter.format(format, arg)
            log(Level.FINE, tuple.message, tuple.throwable)
        }
    }

    override fun debug(format: String, arg1: Any, arg2: Any) {
        if (isDebugEnabled) {
            val tuple = MessageFormatter.format(format, arg1, arg2)
            log(Level.FINE, tuple.message, tuple.throwable)
        }
    }

    override fun debug(format: String, vararg arguments: Any) {
        if (isDebugEnabled) {
            val tuple = MessageFormatter.arrayFormat(format, arguments)
            log(Level.FINE, tuple.message, tuple.throwable)
        }
    }

    override fun debug(message: String, throwable: Throwable) {
        if (isDebugEnabled) {
            log(Level.FINE, message, throwable)
        }
    }

    override fun isInfoEnabled(): Boolean = logger.isLoggable(Level.INFO)

    override fun info(message: String) {
        if (isInfoEnabled) {
            log(Level.INFO, message, null)
        }
    }

    override fun info(format: String, arg: Any) {
        if (isInfoEnabled) {
            val tuple = MessageFormatter.format(format, arg)
            log(Level.INFO, tuple.message, tuple.throwable)
        }
    }

    override fun info(format: String, arg1: Any, arg2: Any) {
        if (isInfoEnabled) {
            val tuple = MessageFormatter.format(format, arg1, arg2)
            log(Level.INFO, tuple.message, tuple.throwable)
        }
    }

    override fun info(format: String, vararg arguments: Any) {
        if (isInfoEnabled) {
            val tuple = MessageFormatter.arrayFormat(format, arguments)
            log(Level.INFO, tuple.message, tuple.throwable)
        }
    }

    override fun info(message: String, throwable: Throwable) {
        if (isInfoEnabled) {
            log(Level.INFO, message, throwable)
        }
    }

    override fun isWarnEnabled(): Boolean = logger.isLoggable(Level.WARNING)

    override fun warn(message: String) {
        if (isWarnEnabled) {
            log(Level.WARNING, message, null)
        }
    }

    override fun warn(format: String, arg: Any) {
        if (isWarnEnabled) {
            val tuple = MessageFormatter.format(format, arg)
            log(Level.WARNING, tuple.message, tuple.throwable)
        }
    }

    override fun warn(format: String, vararg arguments: Any) {
        if (isWarnEnabled) {
            val tuple = MessageFormatter.arrayFormat(format, arguments)
            log(Level.WARNING, tuple.message, tuple.throwable)
        }
    }

    override fun warn(format: String, arg1: Any, arg2: Any) {
        if (isWarnEnabled) {
            val tuple = MessageFormatter.format(format, arg1, arg2)
            log(Level.WARNING, tuple.message, tuple.throwable)
        }
    }

    override fun warn(message: String, throwable: Throwable) {
        if (isWarnEnabled) {
            log(Level.WARNING, message, throwable)
        }
    }

    override fun isErrorEnabled(): Boolean = logger.isLoggable(Level.SEVERE)

    override fun error(message: String) {
        if (isErrorEnabled) {
            log(Level.SEVERE, message, null)
        }
    }

    override fun error(format: String, arg: Any) {
        if (isErrorEnabled) {
            val tuple = MessageFormatter.format(format, arg)
            log(Level.SEVERE, tuple.message, tuple.throwable)
        }
    }

    override fun error(format: String, arg1: Any, arg2: Any) {
        if (isErrorEnabled) {
            val tuple = MessageFormatter.format(format, arg1, arg2)
            log(Level.SEVERE, tuple.message, tuple.throwable)
        }
    }

    override fun error(format: String, vararg arguments: Any) {
        if (isErrorEnabled) {
            val tuple = MessageFormatter.arrayFormat(format, arguments)
            log(Level.SEVERE, tuple.message, tuple.throwable)
        }
    }

    override fun error(message: String, throwable: Throwable) {
        if (isErrorEnabled) {
            log(Level.SEVERE, message, throwable)
        }
    }

    override fun log(marker: Marker, fqcn: String, level: Int, message: String, argArray: Array<Any>, throwable: Throwable) {
        val level0 = when (level) {
            LocationAwareLogger.TRACE_INT -> Level.FINEST
            LocationAwareLogger.DEBUG_INT -> Level.FINE
            LocationAwareLogger.INFO_INT -> Level.INFO
            LocationAwareLogger.WARN_INT -> Level.WARNING
            LocationAwareLogger.ERROR_INT -> Level.SEVERE
            else -> throw IllegalStateException("$level is not a supported logging level!")
        }
        if (logger.isLoggable(level0)) {
            log(level0, message, throwable)
        }
    }

    companion object {
        fun bindLogger(environment: Environment, binder: Binder) {
            binder.bind(org.slf4j.Logger::class.java).toInstance(JavaUtilLoggingAdapter(environment.name))
        }
    }
}