package org.anvilpowered.anvil.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object ServerNodes : UUIDTable("server_nodes") {
    val name = varchar("name", 255).uniqueIndex()
    val gameTypeId = reference("game_type_id", GameTypes)
}

class ServerNode(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by ServerNodes.name
    var gameTypeId by ServerNodes.gameTypeId
    var gameType by GameTypeEntity referencedOn ServerNodes.gameTypeId

    companion object : UUIDEntityClass<ServerNode>(ServerNodes, ServerNode::class.java, ::ServerNode)
}

