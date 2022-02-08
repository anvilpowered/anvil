/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.paper

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.TypeLiteral
import org.anvilpowered.anvil.core.AnvilImpl
import org.anvilpowered.anvil.api.environment.Environment
import org.anvilpowered.anvil.api.environment.EnvironmentBuilderImpl
import org.anvilpowered.anvil.api.registry.AnvilKeys
import org.anvilpowered.anvil.core.command.CommonAnvilCommandNode
import org.anvilpowered.anvil.core.module.CommonModule
import org.anvilpowered.anvil.paper.listener.PaperPlayerListener
import org.anvilpowered.anvil.paper.module.ApiPaperModule
import org.anvilpowered.anvil.paper.module.PaperFallbackModule
import org.anvilpowered.registry.api.ConfigurationService
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class AnvilPaper : JavaPlugin() {
    private val inner: Inner

    init {
        val module: AbstractModule = object : AbstractModule() {
            override fun configure() {
                bind(JavaPlugin::class.java).toInstance(this@AnvilPaper)
                bind(AnvilPaper::class.java).toInstance(this@AnvilPaper)
            }
        }
        val injector = Guice.createInjector(module)
        inner = Inner(injector)
    }

    private inner class Inner(injector: Injector) :
        AnvilImpl(injector, object : CommonModule<CommandSender>("plugins") {}) {
        override fun applyToBuilder(builder: Environment.Builder) {
            super.applyToBuilder(builder)
            builder.addEarlyServices(PaperPlayerListener::class.java) {
                Bukkit.getPluginManager().registerEvents(it, this@AnvilPaper)
            }
            builder.addEarlyServices(object :
                TypeLiteral<CommonAnvilCommandNode<Player, Player, CommandSender>>() {
            })
        }

        override fun whenLoaded(environment: Environment) {
            var serverProxyMode = Bukkit.spigot().config.getBoolean("settings.velocity-support.enabled", false)
            if (!serverProxyMode) {
                serverProxyMode = Bukkit.spigot().config.getBoolean("settings.bungeecord", false)
            }
            val configurationService = environment.injector.getInstance(ConfigurationService::class.java)
            val anvilProxyMode = configurationService.getOrDefault(AnvilKeys.PROXY_MODE)
            if (serverProxyMode && !anvilProxyMode) {
                logger?.error(
                    """
          It looks like you are running this server behind a proxy.
          If this is the case, set server.proxyMode=true in the anvil config.
          """.trimIndent()
                )
            } else if (anvilProxyMode && !serverProxyMode) {
                logger?.error(
                    """
          It looks like you are not running this server behind a proxy.
          If this is the case, set server.proxyMode=false in the anvil config
          """.trimIndent()
                )
            }
        }
    }

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(object : Listener {
            @EventHandler
            fun onLoad(event: ServerLoadEvent) {
                EnvironmentBuilderImpl.completeInitialization(ApiPaperModule(), PaperFallbackModule())
            }
        }, this)
    }

    override fun toString(): String = inner.toString()
}
