package org.anvilpowered.anvil.api.registry

import org.anvilpowered.anvil.api.registry.key.Key

interface RegistryTransaction<T : Any> {

    val key: Key<T>

    val oldValue: T?

    val newValue: T?
}

fun <T : Any> createRegistryTransaction(key: Key<T>, oldValue: T?, newValue: T?): RegistryTransaction<T> {
    return object : RegistryTransaction<T> {
        override val key: Key<T>
            get() = key
        override val oldValue: T?
            get() = oldValue
        override val newValue: T?
            get() = newValue
    }
}
