package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.user.GameUser
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.scope.CrudScope

interface GameUserScope : CrudScope<GameUser, GameUser.CreateDto> {

    suspend fun DomainEntity.Repository<out GameUser>.getAllUserNames(startWith: String = ""): SizedIterable<String>

    suspend fun DomainEntity.Repository<out GameUser>.findByUsername(username: String): GameUser?
}
