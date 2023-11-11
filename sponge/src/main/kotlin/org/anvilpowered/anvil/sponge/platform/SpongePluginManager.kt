package org.anvilpowered.anvil.sponge.platform

import org.anvilpowered.anvil.core.platform.Plugin
import org.anvilpowered.anvil.core.platform.PluginManager
import org.spongepowered.api.Sponge

internal object SpongePluginManager : PluginManager {
    override val plugins: List<Plugin>
        get() = Sponge.pluginManager().plugins().map { it.toAnvilPlugin() }
}
