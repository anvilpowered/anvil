package org.anvilpowered.anvil.sponge.platform

import org.anvilpowered.anvil.core.platform.Plugin
import org.spongepowered.plugin.PluginContainer

internal fun PluginContainer.toAnvilPlugin() = SpongePlugin(this)

internal class SpongePlugin(private val container: PluginContainer) : Plugin {
    override val name: String
        get() = container.metadata().id()
}
