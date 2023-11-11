package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.core.platform.GamePlatform
import org.anvilpowered.anvil.core.platform.Plugin
import org.bukkit.Bukkit

internal object PaperPlatform : GamePlatform {
    override val isProxy: Boolean = false
    override val plugins: List<Plugin>
        get() = Bukkit.getPluginManager().plugins.map { it.toAnvilPlugin() }
    override val name: String
        get() = "paper"
    override val platformVersion: String
        get() = Bukkit.getBukkitVersion()
}
