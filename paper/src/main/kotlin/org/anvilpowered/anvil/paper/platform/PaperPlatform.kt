package org.anvilpowered.anvil.paper.platform

import org.anvilpowered.anvil.core.platform.Platform
import org.anvilpowered.anvil.core.platform.Plugin
import org.bukkit.Bukkit

internal object PaperPlatform : Platform {
    override val isProxy: Boolean = false
    override val plugins: List<Plugin>
        get() = Bukkit.getPluginManager().plugins.map { it.toAnvilPlugin() }
    override val name: String
        get() = "paper"
    override val version: String
        get() = Bukkit.getBukkitVersion()
}
