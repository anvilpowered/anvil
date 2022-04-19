package org.anvilpowered.anvil.api.registry

interface ConfigurationService {

    suspend fun load(registry: MutableRegistry)

    suspend fun save(registry: Registry)
}
