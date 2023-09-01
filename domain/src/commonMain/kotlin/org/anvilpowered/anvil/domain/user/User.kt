package org.anvilpowered.anvil.domain.user

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

/**
 * A user of the Anvil platform.
 *
 * Represents a single universal user across all games and platforms.
 */
data class User(
    override val id: UUID,
    val username: String,
    val email: String? = null,
) : DomainEntity {

    data class CreateDto(
        val username: String,
        val email: String? = null,
    ) : Creates<User>

    /**
     * Operations scoped within a platform context.
     */
    interface PlatformScope {

        val User.gameUser: GameUser

        val User.player: Player?
    }
}
