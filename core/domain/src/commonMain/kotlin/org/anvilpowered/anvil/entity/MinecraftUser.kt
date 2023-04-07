package org.anvilpowered.anvil.entity

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class MinecraftUser(
    val username: String,
    val userId: UUID,
    override val id: UUID,
) : DomainEntity {

    data class CreateDto(
        val username: String,
        val userId: UUID,
        val id: UUID,
    ) : Creates<MinecraftUser>

    companion object Repository : DomainEntity.Repository<MinecraftUser>
}
