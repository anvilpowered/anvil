package org.anvilpowered.anvil.agent

import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent
import org.spongepowered.plugin.builtin.jvm.Plugin

@Plugin("anvil-agent")
class AnvilSpongePlugin {

    @Listener
    fun onServerStart(event: ConstructPluginEvent) {
        println("Hello, world! ${event.plugin()}")
    }
}
