package org.anvilpowered.anvil.entity

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.Crypto
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class GameType(
    val name: String,
    val website: String,
    override val id: UUID = Crypto.randomUUID(),
) : DomainEntity {

    data class CreateDto(
        val name: String,
        val website: String,
    ) : Creates<GameType>

    companion object Repository : DomainEntity.Repository<GameType>
}
