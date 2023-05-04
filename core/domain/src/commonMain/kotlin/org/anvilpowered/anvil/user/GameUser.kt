package org.anvilpowered.anvil.user

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

interface GameUser : DomainEntity, PermissionSubject {
    val username: String
    val userId: UUID

    data class CreateDto(
        val username: String,
        val userId: UUID,
        val id: UUID,
    ) : Creates<GameUser>

    companion object Repository : DomainEntity.Repository<GameUser>
}
