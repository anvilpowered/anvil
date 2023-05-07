package org.anvilpowered.anvil.domain.entity

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class AnvilUser(
    val username: String,
    val email: String,
    val minecraftUsers: List<MinecraftUserData>,
    override val id: UUID,
) : DomainEntity {

    data class CreateDto(
        val username: String,
        val email: String,
    ) : Creates<AnvilUser>

    companion object Repository : DomainEntity.Repository<AnvilUser>
}
