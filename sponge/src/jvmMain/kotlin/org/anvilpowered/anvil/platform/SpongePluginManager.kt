package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.domain.platform.Plugin
import org.anvilpowered.anvil.domain.platform.PluginManager
import org.spongepowered.api.Sponge

internal object SpongePluginManager : PluginManager {
    override val plugins: List<Plugin>
        get() = Sponge.pluginManager().plugins().map { it.toAnvilPlugin() }
}
