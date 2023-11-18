/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
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
import org.anvilpowered.anvil.core.platform.PluginManager
import org.anvilpowered.anvil.core.platform.Server
import org.anvilpowered.anvil.core.user.PlayerService
import org.anvilpowered.anvil.paper.platform.PaperPluginManager
import org.anvilpowered.anvil.paper.platform.PaperServer
import org.anvilpowered.anvil.paper.user.PaperPlayerService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bukkit.plugin.java.JavaPlugin

interface AnvilPaperApi : AnvilApi

context(JavaPlugin)
fun AnvilApi.Companion.createPaper(): AnvilPaperApi {
    return object : AnvilPaperApi {
        override val logger: Logger = LogManager.getLogger(pluginMeta.name)
        override val server: Server = PaperServer
        override val pluginManager: PluginManager = PaperPluginManager
        override val playerService: PlayerService = PaperPlayerService()
    }
}
