package org.anvilpowered.anvil.platform

import org.anvilpowered.anvil.core.platform.Plugin
import org.bukkit.plugin.Plugin as BukkitPlugin

internal fun BukkitPlugin.toAnvilPlugin() = PaperPlugin(this)

class PaperPlugin(private val delegate: BukkitPlugin) : Plugin {
    override val name: String
        get() = delegate.name
}
