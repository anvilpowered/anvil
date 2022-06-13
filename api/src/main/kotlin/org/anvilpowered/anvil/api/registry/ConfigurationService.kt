package org.anvilpowered.anvil.api.registry

interface ConfigurationService {

    suspend fun load(registry: MutableRegistry, schema: ConfigSchema)

    suspend fun save(registry: Registry, schema: ConfigSchema)
}
