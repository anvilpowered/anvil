package org.anvilpowered.anvil.api.registry

interface RegistryUpdatedEvent : RegistryEvent {

    val transactions: List<RegistryTransaction<*>>
}

fun Registry.createUpdateEvent(transactions: List<RegistryTransaction<*>>): RegistryUpdatedEvent {
    return object : RegistryUpdatedEvent {
        override val transactions: List<RegistryTransaction<*>>
            get() = transactions
        override val registry: Registry
            get() = this@createUpdateEvent
    }
}
