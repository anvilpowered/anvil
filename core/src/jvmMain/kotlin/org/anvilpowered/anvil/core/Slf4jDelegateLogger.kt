package org.anvilpowered.anvil.core

import org.lighthousegames.logging.KmLog
import org.slf4j.Logger

class Slf4jDelegateLogger(
    private val delegate: Logger,
) : KmLog(delegate.name) {

    override fun verbose(tag: String, msg: String) {
        super.verbose(tag, msg)
        delegate.trace(msg)
    }

    override fun debug(tag: String, msg: String) {
        super.debug(tag, msg)
        delegate.debug(msg)
    }

    override fun info(tag: String, msg: String) {
        super.info(tag, msg)
        delegate.info(msg)
    }

    override fun warn(tag: String, msg: String, t: Throwable?) {
        super.warn(tag, msg, t)
        if (t != null) {
            delegate.warn(msg, t)
        } else {
            delegate.warn(msg)
        }
    }

    override fun error(tag: String, msg: String, t: Throwable?) {
        super.error(tag, msg, t)
        if (t != null) {
            delegate.error(msg, t)
        } else {
            delegate.error(msg)
        }
    }
}
