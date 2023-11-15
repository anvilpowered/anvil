package org.anvilpowered.anvil.paper.platform

import org.anvilpowered.anvil.core.platform.Plugin
import org.anvilpowered.anvil.core.platform.PluginManager
import org.bukkit.Bukkit

internal object PaperPluginManager : PluginManager {
    override val plugins: List<Plugin>
        get() = Bukkit.getPluginManager().plugins.map { it.toAnvilPlugin() }
}
