package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.GameType
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.scope.CrudScope

interface GameTypeScope : CrudScope<GameType, GameType.CreateDto> {

    suspend fun DomainEntity.Repository<GameType>.findByName(name: String): GameType?

    suspend fun DomainEntity.Repository<GameType>.findByWebsite(website: String): GameType?
}
