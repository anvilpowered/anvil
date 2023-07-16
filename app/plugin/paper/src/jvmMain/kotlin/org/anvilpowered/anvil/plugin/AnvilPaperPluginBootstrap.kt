@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.plugin

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.createPaper
import org.bukkit.plugin.java.JavaPlugin

class AnvilPaperPluginBootstrap : JavaPlugin() {

    private val plugin = with(AnvilApi.createPaper()) {
        AnvilPaperPlugin()
    }

    override fun onLoad() {
        plugin.registerCommands(this)
    }
}
