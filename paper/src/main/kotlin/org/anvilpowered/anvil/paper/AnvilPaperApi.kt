/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2024 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("UnstableApiUsage")

package org.anvilpowered.anvil.paper

import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.CommandExecutor
import org.anvilpowered.anvil.core.platform.PluginManager
import org.anvilpowered.anvil.core.platform.PluginMeta
import org.anvilpowered.anvil.core.platform.Server
import org.anvilpowered.anvil.core.user.PlayerService
import org.anvilpowered.anvil.paper.command.PaperCommandExecutor
import org.anvilpowered.anvil.paper.platform.PaperPluginManager
import org.anvilpowered.anvil.paper.platform.PaperPluginMeta
import org.anvilpowered.anvil.paper.platform.PaperServer
import org.anvilpowered.anvil.paper.user.PaperPlayerService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Path

interface AnvilPaperApi : AnvilApi {
    val plugin: JavaPlugin
}

fun AnvilApi.Companion.createPaper(plugin: JavaPlugin): AnvilPaperApi {
    val logger = LogManager.getLogger(plugin.pluginMeta.name)
    val paperModule = module {
        single<JavaPlugin> { plugin }
        single<Logger> { logger }
        single<Server> { PaperServer }
        single<PluginManager> { PaperPluginManager }
        single<PlayerService> { PaperPlayerService }
        single<PluginMeta> { PaperPluginMeta(plugin.pluginMeta) }
        single { PaperPlayerService }.bind<PlayerService>()
        singleOf(::PaperCommandExecutor).bind<CommandExecutor>()
    }

    return object : AnvilPaperApi {
        override val plugin: JavaPlugin = plugin
        override val logger: Logger = logger
        override val configDir: Path = plugin.dataFolder.toPath()
        override val module: Module = paperModule
    }
}
