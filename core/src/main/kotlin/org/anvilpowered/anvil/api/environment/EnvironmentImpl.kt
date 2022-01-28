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
package org.anvilpowered.anvil.api.environment

import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.Module
import org.anvilpowered.anvil.api.plugin.PluginInfo
import org.anvilpowered.anvil.api.registry.Registry
import java.util.function.Consumer
import java.util.function.Supplier

class EnvironmentImpl internal constructor(
    override var injector: Injector,
    override val name: String,
    override val plugin: Any,
    loggerSupplier: Supplier<*>?,
    modules: MutableCollection<Module>,
    earlyServices: Map<Key<*>, Consumer<*>>
) : Environment {
    private var loggerSupplier: Supplier<*>
    private val modules: MutableCollection<Module>
    val earlyServices: Map<Key<*>, Consumer<*>>

    fun addModule(module: Module) {
        modules.add(module)
    }

    fun getModules(): Collection<Module> {
        return modules
    }

    override fun reload() {
        registry.load()
    }

    override val pluginInfo: PluginInfo
        get() = getInstance(PluginInfo::class.java.canonicalName)

    override val registry: Registry
        get() = injector.getInstance(Registry::class.java)

    override fun compareTo(other: Environment): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        return other is Environment && name == other.name
    }

    override fun toString(): String {
        return name
    }

    init {
        if (loggerSupplier == null) {
            this.loggerSupplier = Supplier<Any?> { null }
        } else {
            this.loggerSupplier = loggerSupplier
        }
        this.modules = modules
        this.earlyServices = earlyServices
    }
}