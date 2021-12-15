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
import org.anvilpowered.anvil.api.Anvil
import org.anvilpowered.anvil.api.registry.Key
import org.anvilpowered.anvil.api.registry.Keys
import org.anvilpowered.anvil.api.registry.Registry
import org.anvilpowered.anvil.api.registry.RegistryScope
import org.anvilpowered.anvil.api.registry.RegistryScoped
import org.slf4j.Logger
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

@Singleton
open class BaseRegistry : Registry {

    @Inject
    protected lateinit var logger: Logger

    val defaultMap: MutableMap<Key<*>, Any>
    private val valueMap: MutableMap<Key<*>?, Any>
    private val listeners: MutableMap<Int, MutableMap<Runnable, RegistryScope>>
    private var coreRegistry: Registry? = null
    private var stringRepresentation: String? = null

    init {
        defaultMap = hashMapOf()
        valueMap = HashMap()
        listeners = hashMapOf()
    }

    override fun <T> getUnsafe(key: Key<T>): T {
        return valueMap[key] as T ?: throw NoSuchElementException("Could not find value for key $key")
    }

    override fun <T> get(key: Key<T>): T? {
        return valueMap[key] as T
    }

    override fun <T> getDefault(key: Key<T>): T {
        val result = defaultMap[key] as T?
        return result ?: key.fallbackValue
    }

    override fun <T> set(key: Key<T>, value: T) {
        valueMap[key] = value!!
        stringRepresentation = null
    }

    @RegistryScoped
    protected fun <T> setDefault(key: Key<T>, value: T) {
        defaultMap[key] = value!!
        stringRepresentation = null
    }

    override fun <T> getExtraSafe(key: Key<T>): T {
        if (coreRegistry == null) {
            coreRegistry = Anvil.registry
        }
        if (this !== coreRegistry
                && getOrDefault(Keys.USE_SHARED_ENVIRONMENT)!!
        ) {
            if (key == Keys.DATA_STORE_NAME
                    || key == Keys.MONGODB_HOSTNAME
                    || key == Keys.MONGODB_PORT
                    || key == Keys.MONGODB_USE_SRV
            ) {
                return coreRegistry!!.getOrDefault(key)
            } else if (getOrDefault(Keys.USE_SHARED_CREDENTIALS)!!) {
                if (key == Keys.MONGODB_USE_CONNECTION_STRING
                        || key == Keys.MONGODB_CONNECTION_STRING
                        || key == Keys.MONGODB_USERNAME
                        || key == Keys.MONGODB_PASSWORD
                        || key == Keys.MONGODB_AUTH_DB
                        || key == Keys.MONGODB_USE_AUTH
                ) {
                    return coreRegistry!!.getOrDefault(key)
                }
            }
        }
        return getOrDefault(key)
    }

    override fun <T> remove(key: Key<T>) {
        valueMap.remove(key)
        stringRepresentation = null
    }

    override fun <T> transform(key: Key<T>, transformer: (Key<T>, T) -> T) {
        valueMap.compute(key, transformer as (Key<*>?, Any?) -> Any?)
        stringRepresentation = null
    }

    override fun <T> transform(key: Key<T>, transformer: Function<in T, out T>) {
        transform(key) { _: Key<T>?, v: T -> transformer.apply(v) }
        stringRepresentation = null
    }

    override fun <T> addToCollection(key: Key<out MutableCollection<T>>, value: T) {
        (valueMap[key] as MutableCollection<T>).add(value)
        stringRepresentation = null
    }

    override fun <T> removeFromCollection(key: Key<out MutableCollection<T>>, value: T) {
        (valueMap[key] as MutableCollection<T>).remove(value)
        stringRepresentation = null
    }

    override fun <K, T> putInMap(key: Key<out MutableMap<K, T>>, mapKey: K, mapValue: T) {
        (valueMap[key] as MutableMap<K, T>)[mapKey] = mapValue
        stringRepresentation = null
    }

    override fun <K, T> removeFromMap(key: Key<out MutableMap<K, T>>, mapKey: K) {
        (valueMap[key] as MutableMap<K, T>).remove(mapKey)
        stringRepresentation = null
    }

    override fun whenLoaded(listener: Runnable): Registry.ListenerRegistrationEnd {
        return ListenerRegistrationEndImpl(listener)
    }

    private inner class ListenerRegistrationEndImpl(private val listener: Runnable) : Registry.ListenerRegistrationEnd {
        private var order = 0
        private var registryScope: RegistryScope

        init {
            registryScope = RegistryScope.DEFAULT
        }

        override fun order(order: Int): Registry.ListenerRegistrationEnd {
            this.order = order
            return this
        }

        override fun scope(registryScope: RegistryScope): Registry.ListenerRegistrationEnd {
            this.registryScope = registryScope
            return this
        }

        override fun register() {
            listeners.computeIfAbsent(order) { HashMap() }[listener] = registryScope
        }
    }

    override fun load(registryScope: RegistryScope) {
        val ordinal: Int = registryScope.ordinal
        if (ordinal <= RegistryScope.DEFAULT.ordinal) {
            loadDefaultScope()
        }
        loadOrdinal(ordinal)
    }

    protected fun loadOrdinal(ordinal: Int) {
        listeners.entries.stream()
                .sorted(java.util.Map.Entry.comparingByKey<Int, Map<Runnable, RegistryScope>>())
                .forEach { (_, value): Map.Entry<Int, Map<Runnable, RegistryScope>> ->
                    value.forEach { (r: Runnable, c: RegistryScope) ->
                        if (ordinal <= c.ordinal) {
                            r.run()
                        }
                    }
                }
    }

    /**
     * Override this method to load values into this registry on normal reloads
     */
    @RegistryScoped
    protected fun loadDefaultScope() {
    }

    override fun toString(): String {
        if (stringRepresentation != null) {
            return stringRepresentation!!
        }
        val keys: MutableSet<Key<*>?> = HashSet()
        val width = intArrayOf(0, 32, 32)
        val addToKeys: Consumer<in Key<*>?> = Consumer { key: Key<*>? ->
            val keyLength = key.toString().length
            if (keyLength > width[0]) {
                width[0] = keyLength
            }
            keys.add(key)
        }
        valueMap.keys.forEach(addToKeys)
        defaultMap.keys.forEach(addToKeys)
        width[0] += 5
        return ("""
  ${String.format("%-" + width[0] + "s", "Key")}${
            String.format("%-" + width[1] + "s",
                    "Value")
        }${String.format("%-" + width[2] + "s", "Default")}

  """.trimIndent()
                + keys.stream().map { key: Key<*>? ->
            String.format("%-" + width[0] + "s", key.toString()) + String.format("%-" + width[1] + "s",
                    valueMap[key]) + String.format("%-" + width[2] + "s", defaultMap[key])
        }.collect(Collectors.joining("\n")).also { stringRepresentation = it })
    }
}
