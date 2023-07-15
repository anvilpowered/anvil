package org.anvilpowered.anvil.core

import org.lighthousegames.logging.KmLog
import java.util.logging.Level
import java.util.logging.Logger

class JULDelegateLogger(
    private val delegate: Logger,
) : KmLog(delegate.name) {
    override fun verbose(tag: String, msg: String) {
        super.verbose(tag, msg)
        delegate.log(Level.FINEST, msg)
    }

    override fun debug(tag: String, msg: String) {
        super.debug(tag, msg)
        delegate.log(Level.FINE, msg)
    }

    override fun info(tag: String, msg: String) {
        super.info(tag, msg)
        delegate.log(Level.INFO, msg)
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        super.warn(tag, msg, t)
        if (t == null) {
            delegate.log(Level.WARNING, msg)
        } else {
            delegate.log(Level.WARNING, msg, t)
        }
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        super.error(tag, msg, t)
        if (t == null) {
            delegate.log(Level.SEVERE, msg)
        } else {
            delegate.log(Level.SEVERE, msg, t)
        }
    }
}
