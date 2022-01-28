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
package org.anvilpowered.anvil.base.registry

import com.google.inject.Inject
import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.ConfigurationService
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.RegistryReloadScope

/**
 * A registry that is backed by the configuration service
 */
@Singleton
open class BaseExtendedRegistry : BaseRegistry() {

    @Inject
    protected lateinit var configurationService: ConfigurationService

    override fun <T> get(key: Key<T>): T? {
        val result = super.get(key)
        return result ?: configurationService[key]
    }

    override fun <T> getDefault(key: Key<T>): T {
        val result = defaultMap[key] as T?
        return result ?: configurationService.getDefault(key)
    }

    override fun load(registryReloadScope: RegistryReloadScope) {
        configurationService.load(registryReloadScope)
        super.load(registryReloadScope)
    }

    override fun toString(): String {
        return """
   ${super.toString()}
   $configurationService
   """.trimIndent()
    }
}
