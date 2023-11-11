package org.anvilpowered.anvil

import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.platform.PaperPlatform
import org.anvilpowered.anvil.platform.PaperPluginManager

interface AnvilPaperApi : AnvilApi

fun AnvilApi.Companion.createPaper(): AnvilPaperApi {
    return object : AnvilPaperApi {
        override val platform = PaperPlatform
        override val pluginManager = PaperPluginManager
    }
}
