package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.domain.platform.Plugin
import org.spongepowered.plugin.PluginContainer

internal fun PluginContainer.toAnvilPlugin() = SpongePlugin(this)

internal class SpongePlugin(private val container: PluginContainer) : Plugin {
    override val name: String
        get() = container.metadata().id()
}
