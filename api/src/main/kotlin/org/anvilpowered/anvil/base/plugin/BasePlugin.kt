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

import com.google.common.reflect.TypeToken
import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import org.anvilpowered.anvil.api.Anvil

/**
 * A helper class for quickly creating an environment. While not strictly necessary, it can
 * simplify the start up process in most cases.
 */
abstract class BasePlugin {
    protected var environment: Environment? = null

    protected constructor(
        name: String?,
        rootInjector: Injector?,
        module: Module?,
        vararg earlyServices: Key<*>?,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this)
    }

    protected constructor(
        name: String?,
        rootInjector: Injector?,
        module: Module?,
        vararg earlyServices: Class<*>?,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this)
    }

    protected constructor(
        name: String?,
        rootInjector: Injector?,
        module: Module?,
        vararg earlyServices: TypeLiteral<*>?,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this)
    }

    protected constructor(
        name: String?,
        rootInjector: Injector?,
        module: Module?,
        vararg earlyServices: TypeToken<*>?,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this)
    }

    protected constructor(
        name: String?,
        rootInjector: Injector?,
        module: Module?,
    ) {
        createDefaultBuilder(name, rootInjector, module)
            .register(this)
    }

    protected fun createDefaultBuilder(
        name: String?,
        rootInjector: Injector?,
        module: Module?,
    ): Environment.Builder {
        val builder: Environment.Builder = Anvil.getEnvironmentBuilder()
            .setName(name)
            .setRootInjector(rootInjector)
            .whenLoaded { environment: Environment? -> whenLoaded(environment) }
            .whenReady { e -> environment = e }
            .whenReady { environment: Environment? -> whenReady(environment) }
            .whenReloaded { environment: Environment? -> whenReloaded(environment) }
        if (module != null) {
            builder.addModules(module)
        }
        applyToBuilder(builder)
        return builder
    }

    protected fun applyToBuilder(builder: Environment.Builder?) {}
    private fun sendLoaded(status: String) {
        val pluginInfo: PluginInfo = environment.getPluginInfo()
        environment.getTextService().builder()
            .append(pluginInfo.prefix)
            .green().append(pluginInfo.version)
            .aqua().append(" by ")
            .appendJoining(", ", pluginInfo.authors)
            .append(" - ", status, "!")
            .sendToConsole()
    }

    protected fun whenLoaded(environment: Environment?) {}
    protected fun whenReady(environment: Environment?) {
        sendLoaded("Loaded")
    }

    protected fun whenReloaded(environment: Environment?) {
        sendLoaded("Reloaded")
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("name", if (environment == null) "null" else environment.getName())
            .toString()
    }
}
