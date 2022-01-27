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
package org.anvilpowered.anvil.base.datastore

import com.google.common.reflect.TypeToken
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.name.Named
import com.google.inject.name.Names
import org.anvilpowered.anvil.api.datastore.DBComponent
import org.anvilpowered.anvil.api.datastore.Manager
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.registry.RegistryScoped
import org.slf4j.Logger
import java.util.Locale

abstract class BaseManager<C : DBComponent<*, *>> protected constructor(protected var registry: Registry) : Manager<C> {

    private val componentType: TypeToken<C>

    @Inject
    private lateinit var injector: Injector

    @Inject
    private lateinit var logger: Logger

    @RegistryScoped
    private var currentComponent: C? = null

    init {
        registry.whenLoaded { registryLoaded() }.register()
        componentType = object : TypeToken<C>(javaClass) {}
    }

    private fun registryLoaded() {
        currentComponent = null
    }

    private fun loadComponent() {
        val dataStoreName: String = registry.getExtraSafe(Keys.DATA_STORE_NAME).lowercase(Locale.ENGLISH)
        val type = componentType.rawType.canonicalName
        val named: Named = Names.named(dataStoreName)
        for ((k, value) in injector.bindings) {
            if (k.typeLiteral.type.typeName.contains(type)
                && named == k.annotation
            ) {
                currentComponent = value.provider.get() as C
                return
            }
        }
        val message = "Anvil: Could not find requested data store: \"$dataStoreName\". Did you bind it correctly?"
        logger.error(message, IllegalStateException(message))
    }

    @RegistryScoped
    override val primaryComponent: C
        get() = try {
            if (currentComponent == null) {
                loadComponent()
            }
            currentComponent!!
        } catch (e: RuntimeException) {
            val message = "Anvil: DataStoreName has not been loaded yet! " +
                "Make sure your Registry and ConfigurationService implementations " +
                "are annotated with @Singleton!"
            logger.error(message)
            throw IllegalStateException(message, e)
        }
}
