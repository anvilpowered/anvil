package org.anvilpowered.anvil

import org.anvilpowered.anvil.api.AnvilApi
import org.anvilpowered.anvil.core.JULDelegateLogger
import org.anvilpowered.anvil.core.db.RepositoryScopeImpl
import org.anvilpowered.anvil.domain.RepositoryScope
import org.anvilpowered.anvil.platform.PaperPlatform
import org.anvilpowered.anvil.platform.PaperPluginManager
import org.bukkit.Bukkit

interface AnvilPaperApi : AnvilApi

fun AnvilApi.Companion.createPaper(): AnvilPaperApi {
    return object : AnvilPaperApi, RepositoryScope by RepositoryScopeImpl {
        override val logger = JULDelegateLogger(Bukkit.getLogger())
        override val platform = PaperPlatform
        override val pluginManager = PaperPluginManager
    }
}
