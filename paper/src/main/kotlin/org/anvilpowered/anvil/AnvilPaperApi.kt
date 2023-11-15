@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil

import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.user.PlayerService
import org.anvilpowered.anvil.platform.PaperPlatform
import org.anvilpowered.anvil.platform.PaperPluginManager
import org.anvilpowered.anvil.user.PaperPlayerService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.plugin.java.JavaPlugin

interface AnvilPaperApi : AnvilApi

context(JavaPlugin)
fun AnvilApi.Companion.createPaper(): AnvilPaperApi {
    return object : AnvilPaperApi {
        override val logger: Logger = LogManager.getLogger(pluginMeta.name)
        override val platform = PaperPlatform
        override val pluginManager = PaperPluginManager
        override val playerService: PlayerService = PaperPlayerService()
    }
}
