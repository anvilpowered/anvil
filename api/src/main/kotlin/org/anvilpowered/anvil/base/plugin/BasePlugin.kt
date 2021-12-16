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
package org.anvilpowered.anvil.base.plugin

import com.google.common.base.MoreObjects
import com.google.common.reflect.TypeToken
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import com.google.inject.TypeLiteral
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.Environment
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.util.SendTextService

/**
 * A helper class for quickly creating an environment. While not strictly necessary, it can
 * simplify the start-up process in most cases.
 */
abstract class BasePlugin {

    protected lateinit var environment: Environment

    protected constructor(
        name: String,
        rootInjector: Injector,
        module: Module,
        vararg earlyServices: Key<*>,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(*earlyServices)
            .register(this)
    }

    protected constructor(
        name: String,
        rootInjector: Injector,
        module: Module,
        vararg earlyServices: Class<*>,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(*earlyServices)
            .register(this)
    }

    protected constructor(
        name: String,
        rootInjector: Injector,
        module: Module,
        vararg earlyServices: TypeLiteral<*>,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(*earlyServices)
            .register(this)
    }

    protected constructor(
        name: String,
        rootInjector: Injector,
        module: Module,
        vararg earlyServices: TypeToken<*>,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(*earlyServices)
            .register(this)
    }

    protected constructor(
        name: String,
        rootInjector: Injector,
        module: Module,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .register(this)
    }

    private fun createDefaultBuilder(
        name: String,
        rootInjector: Injector,
        module: Module?,
    ): Environment.Builder {
        val builder: Environment.Builder = Anvil.environmentBuilder
            .setName(name)
            .setRootInjector(rootInjector)
            .whenLoaded { environment: Environment -> whenLoaded(environment) }
            .whenReady { e -> environment = e }
            .whenReady { environment: Environment -> whenReady(environment) }
            .whenReloaded { environment: Environment -> whenReloaded(environment) }
        if (module != null) {
            builder.addModules(module)
        }
        applyToBuilder(builder)
        return builder
    }

    protected open fun applyToBuilder(builder: Environment.Builder) {}

    private fun sendLoaded(status: String) {
        val pluginInfo: PluginInfo = environment.pluginInfo
        val sendTextService = environment.injector.getInstance(SendTextService::class.java)
        sendTextService.sendToConsole(
            Component.text()
                .append(pluginInfo.prefix)
                .append(Component.text(pluginInfo.version).color(NamedTextColor.GREEN))
                .append(Component.text(" by ").color(NamedTextColor.AQUA))
                .append(Component.text(pluginInfo.authors.toString()))
                .append(Component.text(" - $status!"))
                .build()
        )
    }

    protected open fun whenLoaded(environment: Environment) {}
    private fun whenReady(environment: Environment) {
        sendLoaded("Loaded")
    }

    private fun whenReloaded(environment: Environment) {
        sendLoaded("Reloaded")
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("name", if (environment == null) "null" else environment.name)
            .toString()
    }
}
