package org.anvilpowered.anvil.entity

import org.sourcegrade.kontour.Creates
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

data class User(
    val username: String,
    val email: String,
    val minecraftUsers: List<MinecraftUser>,
    override val id: UUID,
) : DomainEntity {

    data class CreateDto(
        val username: String,
        val email: String,
    ) : Creates<User>

    companion object Repository : DomainEntity.Repository<User>
}
