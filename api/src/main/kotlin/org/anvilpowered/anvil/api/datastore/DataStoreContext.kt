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
package org.anvilpowered.anvil.api.datastore

import com.google.inject.Inject
import org.anvilpowered.anvil.api.registry.Registry
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import java.util.Optional
import java.util.function.Consumer

// TODO: extract to interface
abstract class DataStoreContext<TKey, TDataStore> protected constructor(registry: Registry) {
    private val connectionOpenedListeners: MutableList<Consumer<TDataStore?>>
    private val connectionClosedListeners: MutableList<Consumer<TDataStore>>
    protected val registry: Registry
    private var dataStore: TDataStore? = null
    private lateinit var entityClasses: Array<Class<*>>
    lateinit var tKeyClass: Class<TKey>

    @Inject(optional = true)
    private val classLoader: ClassLoader? = null

    init {
        connectionOpenedListeners = ArrayList()
        connectionClosedListeners = ArrayList()
        this.registry = registry
        registry.whenLoaded { registryLoaded() }.register()
    }

    private fun registryLoaded() {
        requestCloseConnection()
        dataStore = null
    }

    protected abstract fun loadDataStore(): TDataStore
    fun getDataStore(): TDataStore {
        if (dataStore == null) {
            dataStore = loadDataStore()
            notifyConnectionOpenedListeners(dataStore)
        }
        return dataStore ?: throw java.lang.IllegalStateException("An error occurred while loading datastore")
    }

    @SafeVarargs
    protected fun calculateEntityClasses(
        baseScanPackage: String, vararg entityAnnotations: Class<out Annotation>
    ): Array<Class<*>> {
        if (entityAnnotations.isEmpty()) return emptyArray()
        val reflections = Reflections(baseScanPackage, TypeAnnotationsScanner(), SubTypesScanner(), classLoader)
        val types: MutableSet<Class<*>> = reflections.getTypesAnnotatedWith(entityAnnotations[0])
        for (i in 1 until entityAnnotations.size) {
            types.addAll(reflections.getTypesAnnotatedWith(entityAnnotations[i]))
        }
        types.stream().map { entityClasses = entityClasses.plus(it) }
        return entityClasses
    }

    /**
     * @param name The name that the entity class contains
     * @return First entityClass that contains `name`
     */
    fun getEntityClass(name: String): Optional<Class<*>> {
        var clazz: Class<*>? = null
        try {
            clazz = getEntityClassUnsafe(name)
        } catch (ignored: RuntimeException) {
        }
        return Optional.ofNullable(clazz)
    }

    /**
     * @param name The name that the entity class contains
     * @return First entityClass that contains (ignored case) the provided name
     */
    fun getEntityClassUnsafe(name: String): Class<*> {
        getDataStore() // ensure that entityClasses is not null
        val n = name.lowercase()
        for (entityClass in entityClasses) {
            if (entityClass.simpleName.lowercase().contains(n)) {
                return entityClass
            }
        }
        throw IllegalStateException("Could not find EntityClass for $name")
    }

    protected abstract fun closeConnection(dataStore: TDataStore)
    private fun requestCloseConnection() {
        if (dataStore != null) {
            notifyConnectionClosedListeners(dataStore!!)
            closeConnection(dataStore!!)
            dataStore = null
        }
    }

    private fun notifyConnectionOpenedListeners(dataStore: TDataStore?) {
        connectionOpenedListeners.forEach(Consumer { listener: Consumer<TDataStore?> -> listener.accept(dataStore) })
    }

    fun addConnectionOpenedListener(connectionOpenedListener: Consumer<TDataStore?>) {
        connectionOpenedListeners.add(connectionOpenedListener)
    }

    fun removeConnectionOpenedListener(connectionOpenedListener: Consumer<TDataStore?>) {
        connectionOpenedListeners.remove(connectionOpenedListener)
    }

    private fun notifyConnectionClosedListeners(dataStore: TDataStore) {
        connectionClosedListeners.forEach(Consumer { listener: Consumer<TDataStore> -> listener.accept(dataStore) })
    }

    fun addConnectionClosedListener(connectionClosedListener: Consumer<TDataStore>) {
        connectionClosedListeners.add(connectionClosedListener)
    }

    fun removeConnectionClosedListener(connectionClosedListener: Consumer<TDataStore>) {
        connectionClosedListeners.remove(connectionClosedListener)
    }
}
