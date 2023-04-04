package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.DomainEntity
import org.anvilpowered.anvil.entity.GameType
import org.anvilpowered.anvil.entity.GameTypes
import org.anvilpowered.anvil.entity.setValuesFrom
import org.anvilpowered.anvil.entity.toGameType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class GameTypeRepositoryActionsImpl : GameTypeRepositoryActions {

    override suspend fun DomainEntity.Repository<GameType>.save(item: GameType): GameType =
        newSaveTransaction(GameTypes, { setValuesFrom(item) }, ResultRow::toGameType)

    override suspend fun DomainEntity.Repository<GameType>.findById(uuid: UUID): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.id eq uuid).firstOrNull()?.toGameType() }

    override suspend fun DomainEntity.Repository<*>.deleteById(uuid: UUID): Boolean =
        newSuspendedTransaction { GameTypes.deleteWhere { GameTypes.id eq uuid } > 0 }

    override suspend fun DomainEntity.Repository<out GameType>.findByName(name: String): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.name eq name).firstOrNull()?.toGameType() }

    override suspend fun DomainEntity.Repository<out GameType>.findByWebsite(website: String): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.website eq website).firstOrNull()?.toGameType() }
}
