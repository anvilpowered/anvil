/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2026 Contributors
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

package org.anvilpowered.anvil.sponge

import com.google.inject.Injector
import com.google.inject.Key
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.command.CommandExecutor
import org.anvilpowered.anvil.core.platform.PluginManager
import org.anvilpowered.anvil.core.platform.PluginMeta
import org.anvilpowered.anvil.core.platform.Server
import org.anvilpowered.anvil.core.user.PlayerService
import org.anvilpowered.anvil.sponge.command.SpongeCommandExecutor
import org.anvilpowered.anvil.sponge.platform.SpongePluginManager
import org.anvilpowered.anvil.sponge.platform.SpongePluginMeta
import org.anvilpowered.anvil.sponge.platform.SpongeServer
import org.anvilpowered.anvil.sponge.user.SpongePlayerService
import org.apache.logging.log4j.Logger
import org.koin.dsl.bind
import org.koin.dsl.module
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.plugin.metadata.PluginMetadata
import java.nio.file.Path

interface AnvilSpongeApi : AnvilApi {

  companion object
}

/**
 * Creates an Anvil API instance for Sponge.
 *
 * The returned API instance can be used to access the Anvil API.
 * It is intended to be used as a context receiver.
 *
 * For example, you can access it in a class like this:
 * ```kt
 * context(AnvilSpongeApi) // or AnvilApi if in common code
 * class MyPlugin {
 *     fun foo() {
 *         logger.info { "Hello, world!" } // logger property of [AnvilApi] is accessed through context receiver
 *     }
 * }
 * ```
 *
 * In order to invoke this constructor, you must have an instance of [AnvilSpongeApi] in the calling context.
 * To bring an instance of [AnvilSpongeApi] into the calling context, you can use the [with] function:
 *
 * ```kt
 * private val plugin = with(AnvilApi.createSponge(logger, proxyServer)) {
 *     AnvilSpongePlugin()
 * }
 * ```
 *
 * Context receivers may also be used on individual functions.
 * This is particularly useful for top-level functions:
 *
 * ```kt
 * context(AnvilSpongeApi) // or AnvilApi if in common code
 * fun foo() {
 *     logger.info { "Hello, world!" } // logger property of [AnvilApi] is accessed through context receiver
 * }
 * ```
 */
fun AnvilApi.Companion.createSponge(injector: Injector): AnvilSpongeApi {
  val logger = injector.getInstance(Logger::class.java)

  val spongeModule = module {
    single<Logger> { logger }
    single<Server> { SpongeServer }
    single<PluginManager> { SpongePluginManager }
    single<PlayerService> { SpongePlayerService }
    single<PluginMeta> { SpongePluginMeta(injector.getInstance(PluginMetadata::class.java)) }
    single { SpongePlayerService }.bind<PlayerService>()
    single { SpongeCommandExecutor }.bind<CommandExecutor>()
  }

  return object : AnvilSpongeApi {
    override val logger: Logger = logger
    override val configDir: Path = injector.getInstance(Key.get(Path::class.java, ConfigDir(sharedRoot = false)))
    override val module = spongeModule
  }
}
