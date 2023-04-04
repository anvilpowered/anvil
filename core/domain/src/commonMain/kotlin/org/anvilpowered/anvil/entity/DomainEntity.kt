package org.anvilpowered.anvil.entity

import kotlinx.datetime.Instant
import org.anvilpowered.anvil.datastore.UUID

interface DomainEntity {
    val id: UUID
    val createdUtc: Instant
    val updatedUtc: Instant

    interface Repository<T : DomainEntity>
}
