package org.anvilpowered.anvil.api.registry

import kotlinx.coroutines.flow.MutableSharedFlow
import org.anvilpowered.anvil.api.registry.key.Key

abstract class AbstractRegistry(private val delegate: Registry? = null) : MutableRegistry {

    override val flow = MutableSharedFlow<RegistryEvent>()
    private val backing = mutableMapOf<Key<*>, Any>()
    private val default = mutableMapOf<Key<*>, Any>()

    override suspend fun <T : Any> set(key: Key<T>, value: T?): T? {
        val oldValue = get(key)
        flow.emit(createUpdateEvent(listOf(createRegistryTransaction(key, oldValue, value))))
        return oldValue
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: Key<T>): T? = backing[key] as T? ?: delegate?.get(key)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getDefault(key: Key<T>): T? = default[key] as T? ?: delegate?.getDefault(key)
}
