package org.anvilpowered.anvil.domain.user

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

/**
 * A user of a game of the Anvil platform.
 *
 * Represents a single user of a game.
 */
interface GameUser : DomainEntity, PermissionSubject {
    val username: String
    val nickname: String
    val userId: UUID

    data class CreateDto(
        val username: String,
        val userId: UUID,
        val id: UUID,
    ) : Creates<GameUser>

    companion object Repository : DomainEntity.Repository<GameUser>
}
