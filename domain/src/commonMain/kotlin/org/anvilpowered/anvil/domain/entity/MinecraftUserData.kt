package org.anvilpowered.anvil.domain.entity

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class MinecraftUserData(
    val username: String,
    val userId: UUID,
    override val id: UUID,
) : DomainEntity {

    data class CreateDto(
        val username: String,
        val userId: UUID,
        val id: UUID,
    ) : Creates<MinecraftUserData>

    companion object Repository : DomainEntity.Repository<MinecraftUserData>
}
