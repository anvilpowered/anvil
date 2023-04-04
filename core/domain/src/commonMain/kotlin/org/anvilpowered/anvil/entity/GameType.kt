package org.anvilpowered.anvil.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.anvilpowered.anvil.datastore.Crypto
import org.anvilpowered.anvil.datastore.UUID

data class GameType(
    val name: String,
    val website: String,
    override val id: UUID = Crypto.randomUUID(),
    override val createdUtc: Instant = Clock.System.now(), // TODO: UTC now
    override val updatedUtc: Instant = createdUtc,
) : DomainEntity {
    companion object Repository : DomainEntity.Repository<GameType>
}
