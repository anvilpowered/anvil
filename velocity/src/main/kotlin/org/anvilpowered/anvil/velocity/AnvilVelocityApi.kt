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

package org.anvilpowered.anvil.velocity

import com.google.inject.Injector
import com.velocitypowered.api.plugin.PluginDescription
import com.velocitypowered.api.proxy.ProxyServer
import org.anvilpowered.anvil.core.AnvilApi
import org.anvilpowered.anvil.core.user.PlayerService
import org.anvilpowered.anvil.velocity.platform.VelocityPlatform
import org.anvilpowered.anvil.velocity.platform.VelocityPluginManager
import org.anvilpowered.anvil.velocity.user.VelocityPlayerService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * A subtype of [AnvilApi] that also provides access to Velocity-specific APIs such as [ProxyServer].
 *
 * To create an instance of this interface, use [AnvilApi.Companion.createVelocity].
 *
 * If you are using Java, the method [AnvilVelocityApi.doNotUse] is provided as an alternative.
 */
interface AnvilVelocityApi : AnvilApi, ProxyServerScope {

    companion object {
        /**
         * Creates an Anvil API instance for Velocity.
         *
         * This method is meant as an alternative to [AnvilApi.Companion.createVelocity] for Java users.
         *
         * In Kotlin, you should use [AnvilApi.Companion.createVelocity] instead.
         */
        @JvmStatic
        @JvmName("create")
        fun doNotUse(injector: Injector): AnvilVelocityApi =
            AnvilApi.createVelocity(injector)
    }
}

/**
 * Creates an Anvil API instance for Velocity.
 *
 * The returned API instance can be used to access the Anvil API.
 * It is intended to be used as a context receiver.
 *
 * For example, you can access it in a class like this:
 * ```kt
 * context(AnvilVelocityApi) // or AnvilApi if in common code
 * class MyPlugin {
 *     fun foo() {
 *         logger.info { "Hello, world!" } // logger property of [AnvilApi] is accessed through context receiver
 *     }
 * }
 * ```
 *
 * In order to invoke this constructor, you must have an instance of [AnvilVelocityApi] in the calling context.
 * To bring an instance of [AnvilVelocityApi] into the calling context, you can use the [with] function:
 *
 * ```kt
 * private val plugin = with(AnvilApi.createVelocity(logger, proxyServer)) {
 *     AnvilVelocityPlugin()
 * }
 * ```
 *
 * Context receivers may also be used on individual functions.
 * This is particularly useful for top-level functions:
 *
 * ```kt
 * context(AnvilVelocityApi) // or AnvilApi if in common code
 * fun foo() {
 *     logger.info { "Hello, world!" } // logger property of [AnvilApi] is accessed through context receiver
 * }
 * ```
 */
fun AnvilApi.Companion.createVelocity(injector: Injector): AnvilVelocityApi {
    val proxyServer = injector.getInstance(ProxyServer::class.java)
    val pluginDescription = injector.getInstance(PluginDescription::class.java)
    return object : AnvilVelocityApi {
        override val logger: Logger = LogManager.getLogger(pluginDescription.id)
        override val platform = VelocityPlatform(proxyServer)
        override val pluginManager = VelocityPluginManager(proxyServer.pluginManager)
        override val proxyServer: ProxyServer = proxyServer
        override val playerService: PlayerService = VelocityPlayerService()
    }
}
