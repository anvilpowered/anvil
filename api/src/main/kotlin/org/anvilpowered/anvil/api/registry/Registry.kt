/*
 *   Anvil - Registry
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
package org.anvilpowered.anvil.api.registry

import com.google.inject.Singleton
import org.anvilpowered.anvil.api.registry.key.Key
import org.anvilpowered.anvil.api.registry.scope.RegistryReloadScope
import org.anvilpowered.anvil.api.registry.scope.RegistryScoped
import java.util.function.Function

@Singleton
open class Registry {

    val defaultMap: MutableMap<Key<*>, Any>
    private val valueMap: MutableMap<Key<*>?, Any>
    private val listeners: MutableMap<Int, MutableMap<Runnable, RegistryReloadScope>>
    private var stringRepresentation: String? = null

    init {
        defaultMap = hashMapOf()
        valueMap = HashMap()
        listeners = hashMapOf()
    }

    /**
     * Gets this registry's value for the provided [Key]
     * and throws an exception if it is not present.
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to get the value for
     * @return This registry's value for the provided [Key]
     * @throws NoSuchElementException If this registry has no value defined
     * for the provided [Key]
    </T> */
    @RegistryScoped
    open fun <T> getUnsafe(key: Key<T>): T {
        return valueMap[key] as T ?: throw NoSuchElementException("Could not find value for key $key")
    }

    /**
     * Gets this registry's value for the provided [Key]
     * or null if it is not present.
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to get the value for
     * @return This registry's value for the provided [Key] or null
    </T> */
    @RegistryScoped
    operator fun <T> get(key: Key<T>): T? {
        return valueMap[key] as T
    }

    /**
     * Gets this registry's default value for the provided [Key]
     * or the fallback value if it is not present.
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to get the default value for
     * @return This registry's default value for the provided [Key] or the fallback value
    </T> */
    @RegistryScoped
    fun <T> getDefault(key: Key<T>): T {
        val result = defaultMap[key] as T?
        return result ?: key.fallbackValue
        ?: throw IllegalStateException("Could not find a default value for ${key.name}")
    }

    /**
     * Gets this registry's value for the provided [Key]
     * or the default value if it is not present.
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to get the value or (if not present) the default value for
     * @return This registry's value for the provided [Key] or the default value
    </T> */
    @RegistryScoped
    fun <T> getOrDefault(key: Key<T>): T {
        return get(key) ?: getDefault(key)
    }

    /**
     * Sets this registry's value for the provided [Key]
     *
     * @param <T>   The value type of the provided [Key]
     * @param key   The [Key] to set the value for
     * @param value The value to set
    </T> */
    @RegistryScoped
    open operator fun <T> set(key: Key<T>, value: T) {
        valueMap[key] = value!!
        stringRepresentation = null
    }

    /**
     * Removes this registry's value for the provided [Key]
     *
     * @param <T> The value type of the provided [Key]
     * @param key The [Key] to set the value for
    </T> */
    @RegistryScoped
    open fun <T> remove(key: Key<T>) {
        valueMap.remove(key)
        stringRepresentation = null
    }

    /**
     * Applies the provided transformation to this registry's
     * value for the provided [Key]
     *
     * @param <T>         The value type of the provided [Key]
     * @param key         The [Key] to transform the value for
     * @param transformer The transformation to apply
    </T> */
    @RegistryScoped
    open fun <T> transform(key: Key<T>, transformer: (Key<T>, T) -> T) {
        valueMap.compute(key, transformer as (Key<*>?, Any?) -> Any?)
        stringRepresentation = null
    }


    /**
     * Applies the provided transformation to this registry's
     * value for the provided [Key]
     *
     * @param <T>         The value type of the provided [Key]
     * @param key         The [Key] to transform the value for
     * @param transformer The transformation to apply
    </T> */
    @RegistryScoped
    open fun <T> transform(key: Key<T>, transformer: Function<in T, out T>) {
        transform(key) { _: Key<T>?, v: T -> transformer.apply(v) }
        stringRepresentation = null
    }

    /**
     * Adds the provided value to this registry's
     * [Collection] value for the provided [Key]
     *
     * @param <T>   The value type of the provided [Key]
     * @param key   The [Key] of the collection
     * to add the provided value to
     * @param value The value to add
    </T> */
    @RegistryScoped
    open fun <T> addToCollection(key: Key<out MutableCollection<T>>, value: T) {
        (valueMap[key] as MutableCollection<T>).add(value)
        stringRepresentation = null
    }

    /**
     * Removes the provided value from this registry's
     * [Collection] value for the provided [Key]
     *
     * @param <T>   The value type of the provided [Key]
     * @param key   The [Key] of the collection
     * to add the provided value to
     * @param value The value to add
    </T> */
    @RegistryScoped
    open fun <T> removeFromCollection(key: Key<out MutableCollection<T>>, value: T) {
        (valueMap[key] as MutableCollection<T>).remove(value)
        stringRepresentation = null
    }

    /**
     * Puts the provided key and value pair to this registry's
     * [Map] value for the provided [Key]
     *
     * @param <K>      The key type of the map value for the provided key
     * @param <T>      The value type of the map value for the provided key
     * @param key      The [Key] of the map to add the
     * provided key and value pair to
     * @param mapKey   The map key to add
     * @param mapValue The map value to add
    </T></K> */
    @RegistryScoped
    open fun <K, T> putInMap(key: Key<out MutableMap<K, T>>, mapKey: K, mapValue: T) {
        (valueMap[key] as MutableMap<K, T>)[mapKey] = mapValue
        stringRepresentation = null
    }

    /**
     * Removes the provided key from this registry's
     * [Map] value for the provided [Key]
     *
     * @param <K>    The key type of the map value for the provided key
     * @param <T>    The value type of the map value for the provided key
     * @param key    The [Key] of the map to remove the provided mapKey from
     * @param mapKey The map key to remove
    </T></K> */
    @RegistryScoped
    open fun <K, T> removeFromMap(key: Key<out MutableMap<K, T>>, mapKey: K) {
        (valueMap[key] as MutableMap<K, T>).remove(mapKey)
        stringRepresentation = null
    }

    @RegistryScoped
    fun <T> setDefault(key: Key<T>, value: T) {
        defaultMap[key] = value!!
        stringRepresentation = null
    }

    /**
     * Runs all [listeners][Runnable] that were
     * added before this call in the provided registryScope.
     *
     * @param registryReloadScope The [RegistryReloadScope] to load
     * @see Environment.reload
     * @see .whenLoaded
     */
    @RegistryScoped
    open fun load(registryReloadScope: RegistryReloadScope)  {
        val ordinal: Int = registryReloadScope.ordinal
        if (ordinal <= RegistryReloadScope.DEFAULT.ordinal) {
            loadDefaultScope()
        }
        loadOrdinal(ordinal)
    }

    private fun loadOrdinal(ordinal: Int) {
        listeners.entries.stream()
            .sorted(java.util.Map.Entry.comparingByKey<Int, Map<Runnable, RegistryReloadScope>>())
            .forEach { (_, value): Map.Entry<Int, Map<Runnable, RegistryReloadScope>> ->
                value.forEach { (r: Runnable, c: RegistryReloadScope) ->
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

    /**
     * Runs all [listeners][Runnable] that were
     * added before this call in the [default scope][RegistryReloadScope.DEFAULT].
     *
     * @see Environment.reload
     * @see .whenLoaded
     */
    @RegistryScoped
    fun load() {
        load(RegistryReloadScope.DEFAULT)
    }

    /**
     * Adds a [Runnable] to be loaded on [.load].
     *
     *
     *
     * Listeners are grouped by order. Smaller orders run before larger ones.
     * The execution order within one order group is not guaranteed.
     *
     *
     *
     * Please note that [ListenerRegistrationEnd.register] must be invoked to
     * complete the registration.
     *
     *
     * @param listener Listener to add
     * @return A [ListenerRegistrationEnd] for specifying additional parameters and
     * completing the registration.
     * @see .load
     */
    open fun whenLoaded(listener: Runnable): ListenerRegistrationEnd {
        return ListenerRegistrationEnd(listener)
    }

    inner class ListenerRegistrationEnd(private val listener: Runnable) {
        private var order = 0
        private var registryReloadScope = RegistryReloadScope.DEFAULT

        /**
         * Sets the order for the listener.
         * The default order is 0.
         *
         * @param order The order to run this listener in. Smaller is earlier.
         * @return `this`
         */
         fun order(order: Int): ListenerRegistrationEnd {
            this.order = order
            return this
        }

        /**
         * Sets the scope for the listener.
         * The default scope is [RegistryReloadScope.DEFAULT].
         *
         * @param scope The scope to run this listener in.
         * @return `this`
         * @see RegistryReloadScope
         *
         * @see RegistryScoped
         */
        fun scope(scope: RegistryReloadScope): ListenerRegistrationEnd {
            this.registryReloadScope = scope
            return this
        }

        /**
         * Completes the listener registration.
         */
        fun register() {
            listeners.computeIfAbsent(order) { hashMapOf()} [listener] = registryReloadScope
        }
    }
}
