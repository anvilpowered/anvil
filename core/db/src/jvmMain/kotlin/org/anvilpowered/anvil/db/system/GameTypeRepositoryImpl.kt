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

import org.anvilpowered.anvil.domain.system.GameType
import org.anvilpowered.anvil.domain.system.GameTypeRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.Dto
import org.sourcegrade.kontour.UUID
import kotlin.reflect.KClass

internal object GameTypeRepositoryImpl : GameTypeRepository {
    override suspend fun findByName(name: String): GameType? = newSuspendedTransaction {
        GameTypeEntity.find(GameTypeTable.name eq name).firstOrNull()
            ?.let { GameType(it.id.value) }
    }

    override suspend fun findByWebsite(website: String): GameType? = newSuspendedTransaction {
        GameTypeEntity.find(GameTypeTable.website eq website).firstOrNull()
            ?.let { GameType(it.id.value) }
    }

    override suspend fun countAll(): Long = newSuspendedTransaction {
        GameTypeEntity.all().count()
    }

    override suspend fun create(item: GameType.CreateDto): GameType = newSuspendedTransaction {
        GameTypeEntity.new {
            name = item.name
            website = item.website
        }.let { GameType(it.id.value) }
    }

    override suspend fun <D : Dto<GameType>> findDtoById(id: UUID, dtoType: KClass<D>): D? {
        TODO()
    }

    override suspend fun exists(id: UUID): Boolean = newSuspendedTransaction {
        GameTypeEntity.findById(id) != null
    }

    override suspend fun deleteById(id: UUID): Boolean = newSuspendedTransaction {
        GameTypeTable.deleteWhere { GameTypeTable.id eq id } > 0
    }
}
