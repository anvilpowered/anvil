package org.anvilpowered.anvil.domain.entity

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.Crypto
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class ServerNode(
    val name: String,
    val gameType: GameType,
    override val id: UUID = Crypto.randomUUID(),
) : DomainEntity {

    data class CreateDto(
        val name: String,
        val gameTypeId: UUID,
    ) : Creates<ServerNode>

    companion object Repository : DomainEntity.Repository<ServerNode>
}
