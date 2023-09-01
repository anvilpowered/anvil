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

import org.anvilpowered.anvil.domain.system.ServerNode
import org.anvilpowered.anvil.domain.system.ServerNodeRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.Dto
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.UUID
import kotlin.reflect.KClass

internal object ServerNodeRepositoryImpl : ServerNodeRepository {

    override suspend fun exists(id: UUID): Boolean =
        newSuspendedTransaction { ServerNodeEntity.findById(id) != null }

    override suspend fun findByGameType(): GameTypeJoin<SizedIterable<ServerNode>> = GameTypeJoinImpl
    override suspend fun countAll(): Long = newSuspendedTransaction { ServerNodeEntity.all().count() }

    override suspend fun create(item: ServerNode.CreateDto): ServerNode = TODO()
    override suspend fun <D : Dto<ServerNode>> findDtoById(id: UUID, dtoType: KClass<D>): D? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: UUID): Boolean =
        newSuspendedTransaction { ServerNodeTable.deleteWhere { ServerNodeTable.id eq id } > 0 }

    private object GameTypeJoinImpl : GameTypeJoin<SizedIterable<ServerNode>> {
        override suspend fun id(id: UUID): SizedIterable<ServerNode> =
            newSuspendedTransaction {
//               val t= (ServerNodeTable innerJoin GameTypes).select { GameTypes.id eq id }.mapLazy { it.toServerNode() }

                (GameTypeEntity.findById(id) ?: throw NoSuchElementException())
                    .serverNodes

                TODO()
            }

        override suspend fun name(name: String): SizedIterable<ServerNode> =
            newSuspendedTransaction {
//                (ServerNodeTable innerJoin GameTypes).select { GameTypes.name eq name }.mapLazy { it.toServerNode() }
                TODO()
            }

        override suspend fun website(website: String): SizedIterable<ServerNode> =
            newSuspendedTransaction {
//                (ServerNodeTable innerJoin GameTypes).select { GameTypes.website eq website }.mapLazy { it.toServerNode() }
                TODO()
            }
    }
}
