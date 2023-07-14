package org.anvilpowered.anvil.domain.user

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

/**
 * A user of a game of the Anvil platform.
 *
 * Represents a single user of a game.
 */
data class GameUser(override val id: UUID) : DomainEntity {

    data class CreateDto(
        val id: UUID,
        val userId: UUID,
        val gameTypeId: UUID,
        val username: String,
    ) : Creates<GameUser>

    interface GamePlatformScope { // TODO: Maybe just GameScope?

        val GameUser.subject: Subject?

        val GameUser.player: Player?
    }
}
