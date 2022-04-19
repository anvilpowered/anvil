package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key

interface MutableRegistry : Registry {

    /**
     * Sets this registry's value for the provided [Key]
     *
     * @param <T>   The value type of the provided [Key]
     * @param key   The [Key] to set the value for, null removes the mapping
     * @param value The value to set
     */
    suspend fun <T : Any> set(key: Key<T>, value: T?): T?
}
