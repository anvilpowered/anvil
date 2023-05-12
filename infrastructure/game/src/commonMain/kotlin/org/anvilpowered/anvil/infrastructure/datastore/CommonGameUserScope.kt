package org.anvilpowered.anvil.infrastructure.datastore

import org.anvilpowered.anvil.domain.datastore.GameUserScope
import org.anvilpowered.anvil.domain.user.GameUser
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.UUID

class CommonGameUserScope : GameUserScope {
    override suspend fun DomainEntity.Repository<out GameUser>.getAllUserNames(startWith: String): SizedIterable<String> {
        TODO("Not yet implemented")
    }

    override suspend fun DomainEntity.Repository<out GameUser>.findByUsername(username: String): GameUser? {
        TODO("Not yet implemented")
    }

    override suspend fun DomainEntity.Repository<GameUser>.create(item: GameUser.CreateDto): GameUser {
        TODO("Not yet implemented")
    }

    override suspend fun DomainEntity.Repository<GameUser>.findById(id: UUID): GameUser? {
        TODO("Not yet implemented")
    }

    override suspend fun DomainEntity.Repository<GameUser>.deleteById(id: UUID): Boolean {
        TODO("Not yet implemented")
    }
}
