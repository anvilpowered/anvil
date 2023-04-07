package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.GameType
import org.anvilpowered.anvil.entity.GameTypes
import org.anvilpowered.anvil.entity.setValuesFrom
import org.anvilpowered.anvil.entity.toGameType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

internal object GameTypeScopeImpl : GameTypeScope {

    override suspend fun DomainEntity.Repository<GameType>.create(item: GameType.CreateDto): GameType =
        newSaveTransaction(GameTypes, { setValuesFrom(item) }, ResultRow::toGameType)

    override suspend fun DomainEntity.Repository<GameType>.findById(id: UUID): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.id eq id).firstOrNull()?.toGameType() }

    override suspend fun DomainEntity.Repository<GameType>.deleteById(id: UUID): Boolean =
        newSuspendedTransaction { GameTypes.deleteWhere { GameTypes.id eq id } > 0 }

    override suspend fun DomainEntity.Repository<GameType>.findByName(name: String): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.name eq name).firstOrNull()?.toGameType() }

    override suspend fun DomainEntity.Repository<GameType>.findByWebsite(website: String): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.website eq website).firstOrNull()?.toGameType() }
}
