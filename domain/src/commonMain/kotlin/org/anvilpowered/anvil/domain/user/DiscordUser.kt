package org.anvilpowered.anvil.domain.user

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class DiscordUser(override val id: UUID) : DomainEntity {

    data class CreateDto(
        val id: UUID,
        val discordId: String,
    ) : Creates<DiscordUser>
}
