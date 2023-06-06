package org.anvilpowered.anvil.entity

import org.anvilpowered.anvil.db.system.DbGameType
import org.anvilpowered.anvil.db.system.DbGameType.Companion.referrersOn
import org.anvilpowered.anvil.db.system.GameTypes
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SizedIterable
import org.sourcegrade.kontour.UUID

object ServerNodes : UUIDTable("server_nodes") {
    val name = varchar("name", 255).uniqueIndex()
    val gameTypeId = reference("game_type_id", GameTypes)
}

class DbServerNode(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by ServerNodes.name
    var gameTypeId by ServerNodes.gameTypeId
    var gameType by DbGameType referencedOn ServerNodes.gameTypeId

    companion object : UUIDEntityClass<DbServerNode>(ServerNodes)
}

val DbGameType.serverNodes: SizedIterable<DbServerNode> by DbServerNode referrersOn ServerNodes.gameTypeId
