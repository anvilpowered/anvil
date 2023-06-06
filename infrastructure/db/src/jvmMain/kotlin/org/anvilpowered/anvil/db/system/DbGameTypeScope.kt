/*
 *   Anvil - AnvilPowered.org
 *   Copyright (C) 2019-2023 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.db.system

import org.anvilpowered.anvil.db.datastore.newSaveTransaction
import org.anvilpowered.anvil.db.user.setValuesFrom
import org.anvilpowered.anvil.domain.system.GameType
import org.anvilpowered.anvil.entity.toGameType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.UUID

internal class DbGameTypeScope : GameType.DbScope {

    override suspend fun DomainEntity.Repository<GameType>.create(item: GameType.CreateDto): GameType =
        newSaveTransaction(GameTypes, { org.anvilpowered.anvil.db.user.setValuesFrom(item) }, ResultRow::toGameType)

    override suspend fun DomainEntity.Repository<GameType>.findById(id: UUID): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.id eq id).firstOrNull()?.toGameType() }

    override suspend fun DomainEntity.Repository<GameType>.deleteById(id: UUID): Boolean =
        newSuspendedTransaction { GameTypes.deleteWhere { GameTypes.id eq id } > 0 }

    override suspend fun DomainEntity.Repository<GameType>.findByName(name: String): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.name eq name).firstOrNull()?.toGameType() }

    override suspend fun DomainEntity.Repository<GameType>.findByWebsite(website: String): GameType? =
        newSuspendedTransaction { GameTypes.select(GameTypes.website eq website).firstOrNull()?.toGameType() }
}
