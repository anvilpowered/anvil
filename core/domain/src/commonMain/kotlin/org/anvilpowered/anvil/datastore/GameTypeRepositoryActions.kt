package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.DomainEntity
import org.anvilpowered.anvil.entity.GameType

interface GameTypeRepositoryActions : RepositoryActions<GameType> {

    suspend fun DomainEntity.Repository<out GameType>.findByName(name: String): GameType?

    suspend fun DomainEntity.Repository<out GameType>.findByWebsite(website: String): GameType?
}
