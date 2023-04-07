package org.anvilpowered.anvil.datastore

import org.anvilpowered.anvil.entity.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.mapLazy
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.sourcegrade.kontour.DomainEntity
import org.sourcegrade.kontour.SizedIterable
import org.sourcegrade.kontour.UUID

internal object ServerNodeScopeImpl : ServerNodeScope {

    override suspend fun DomainEntity.Repository<ServerNode>.findById(id: UUID): ServerNode? =
        newSuspendedTransaction {
            (ServerNodes innerJoin GameTypes).select { ServerNodes.id eq id }.firstOrNull()?.toServerNode()
        }

    override fun DomainEntity.Repository<ServerNode>.findByGameType(): GameTypeJoin<SizedIterable<ServerNode>> = GameTypeJoinImpl

    override suspend fun DomainEntity.Repository<ServerNode>.create(item: ServerNode.CreateDto): ServerNode =
        newSaveTransaction(ServerNodes, { setValuesFrom(item) }, ResultRow::toServerNode)

    override suspend fun DomainEntity.Repository<ServerNode>.deleteById(id: UUID): Boolean =
        newSuspendedTransaction { ServerNodes.deleteWhere { ServerNodes.id eq id } > 0 }

    private object GameTypeJoinImpl : GameTypeJoin<SizedIterable<ServerNode>> {
        override suspend fun id(id: UUID): SizedIterable<ServerNode> =
            newSuspendedTransaction {
                (ServerNodes innerJoin GameTypes).select { GameTypes.id eq id }.mapLazy { it.toServerNode() }
            }

        override suspend fun name(name: String): SizedIterable<ServerNode> =
            newSuspendedTransaction {
                (ServerNodes innerJoin GameTypes).select { GameTypes.name eq name }.mapLazy { it.toServerNode() }
            }

        override suspend fun website(website: String): SizedIterable<ServerNode> =
            newSuspendedTransaction {
                (ServerNodes innerJoin GameTypes).select { GameTypes.website eq website }.mapLazy { it.toServerNode() }
            }
    }
}
