package org.anvilpowered.anvil.domain.user

import org.anvilpowered.anvil.domain.system.GameType
import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.UUID
import org.sourcegrade.kontour.scope.CrudScope

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

    interface PlatformScope : UserFacetScope<GameUser> {

        val GameUser.subject: Subject

        val GameUser.player: Player?
    }

    interface DbScope : CrudScope<GameUser, CreateDto> {

        suspend fun GameUser.getUserId(): UUID

        suspend fun GameUser.getGameType(): GameType

        suspend fun GameUser.getNickname(): String?

        suspend fun DomainEntity.Repository<GameUser>.getAllUserNames(startWith: String = ""): SizedIterable<String>

        suspend fun DomainEntity.Repository<GameUser>.findByUsername(username: String): GameUser?
    }

    companion object Repository : DomainEntity.Repository<GameUser>
}
