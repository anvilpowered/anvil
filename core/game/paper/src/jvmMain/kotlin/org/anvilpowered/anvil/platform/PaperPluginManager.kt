package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.domain.platform.Plugin
import org.anvilpowered.anvil.domain.platform.PluginManager
import org.bukkit.Bukkit

internal object PaperPluginManager : PluginManager {
    override val plugins: List<Plugin>
        get() = Bukkit.getPluginManager().plugins.map { it.toAnvilPlugin() }
}
