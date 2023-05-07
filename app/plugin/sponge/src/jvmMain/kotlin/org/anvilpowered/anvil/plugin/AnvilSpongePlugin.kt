package org.anvilpowered.anvil.plugin

import com.google.inject.Inject
import org.apache.logging.log4j.Logger
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent
import org.spongepowered.plugin.builtin.jvm.Plugin

@Plugin("anvil-agent")
class AnvilSpongePlugin @Inject constructor(
    private val logger: Logger,
) {

    @Listener
    fun onServerStart(event: ConstructPluginEvent) {
        logger.warn("Hello, world! ${event.plugin()}")
    }
}
