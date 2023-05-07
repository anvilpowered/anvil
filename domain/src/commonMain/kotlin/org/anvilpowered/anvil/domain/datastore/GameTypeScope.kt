package org.anvilpowered.anvil.domain.datastore

import org.anvilpowered.anvil.domain.entity.GameType
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.scope.CrudScope

interface GameTypeScope : CrudScope<GameType, GameType.CreateDto> {

    suspend fun DomainEntity.Repository<GameType>.findByName(name: String): GameType?

    suspend fun DomainEntity.Repository<GameType>.findByWebsite(website: String): GameType?
}
