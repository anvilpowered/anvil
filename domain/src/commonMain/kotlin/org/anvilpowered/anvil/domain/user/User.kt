package org.anvilpowered.anvil.domain.user

import org.anvilpowered.anvil.domain.system.GameType
import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID
import org.sourcegrade.kontour.scope.CrudScope

/**
 * A user of the Anvil platform.
 *
 * Represents a single universal user across all games and platforms.
 */
data class User(override val id: UUID) : DomainEntity {

    data class CreateDto(
        val username: String,
        val email: String,
    ) : Creates<User>

    /**
     * Operations scoped within a platform context.
     */
    interface PlatformScope {

        val User.gameUser: GameUser

        val User.player: Player?
    }

    /**
     * Operations scoped within a database context.
     */
    interface DbScope : CrudScope<User, CreateDto> {

        suspend fun User.getUsername(): String

        suspend fun User.getEmail(): String

        suspend fun User.getGameUsers(gameType: GameType? = null): List<GameUser>

        suspend fun DomainEntity.Repository<User>.findByUsername(username: String): User?
    }

    companion object Repository : DomainEntity.Repository<User>
}
