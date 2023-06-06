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
import org.anvilpowered.anvil.domain.system.GameTypeJoin
import org.anvilpowered.anvil.domain.system.ServerNode
import org.anvilpowered.anvil.entity.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.UUID

internal object DbServerNodeScope : ServerNode.DbScope {

    override suspend fun DomainEntity.Repository<ServerNode>.findById(id: UUID): ServerNode? =
        newSuspendedTransaction {
            DbServerNode.findById(id)?.let { ServerNode(it.id.value) }
        }

    override suspend fun ServerNode.getName(): String = newSuspendedTransaction {
        (DbServerNode.findById(this@getName.id) ?: throw NoSuchElementException())
            .name
    }

    override suspend fun ServerNode.getGameType(): GameType = newSuspendedTransaction {
        val resultId = (DbServerNode.findById(this@getGameType.id) ?: throw NoSuchElementException())
            .gameTypeId.value
        GameType(resultId)
    }

    override suspend fun DomainEntity.Repository<ServerNode>.findByGameType(): GameTypeJoin<SizedIterable<ServerNode>> = GameTypeJoinImpl

    override suspend fun DomainEntity.Repository<ServerNode>.create(item: ServerNode.CreateDto): ServerNode = TODO()
//        newSaveTransaction(ServerNodes, { setValuesFrom(item) }, ResultRow::toServerNode)

    override suspend fun DomainEntity.Repository<ServerNode>.deleteById(id: UUID): Boolean =
        newSuspendedTransaction { ServerNodes.deleteWhere { ServerNodes.id eq id } > 0 }

    private object GameTypeJoinImpl : GameTypeJoin<SizedIterable<ServerNode>> {
        override suspend fun id(id: UUID): SizedIterable<ServerNode> =
            newSuspendedTransaction {
//               val t= (ServerNodes innerJoin GameTypes).select { GameTypes.id eq id }.mapLazy { it.toServerNode() }

                (DbGameType.findById(id) ?: throw NoSuchElementException())
                    .serverNodes

                TODO()
            }

        override suspend fun name(name: String): SizedIterable<ServerNode> =
            newSuspendedTransaction {
                (ServerNodes innerJoin GameTypes).select { GameTypes.name eq name }.mapLazy { it.toServerNode() }
                TODO()
            }

        override suspend fun website(website: String): SizedIterable<ServerNode> =
            newSuspendedTransaction {
                (ServerNodes innerJoin GameTypes).select { GameTypes.website eq website }.mapLazy { it.toServerNode() }
                TODO()
            }
    }
}
