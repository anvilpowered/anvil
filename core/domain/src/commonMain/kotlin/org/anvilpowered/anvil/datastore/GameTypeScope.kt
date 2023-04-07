package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.GameType
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.scope.CreateScope
import org.sourcegrade.kontour.scope.DeleteScope
import org.sourcegrade.kontour.scope.FindScope

interface GameTypeScope :
    CreateScope<GameType, GameType.CreateDto>,
    FindScope<GameType>,
    DeleteScope<GameType> {

    suspend fun DomainEntity.Repository<GameType>.findByName(name: String): GameType?

    suspend fun DomainEntity.Repository<GameType>.findByWebsite(website: String): GameType?
}
