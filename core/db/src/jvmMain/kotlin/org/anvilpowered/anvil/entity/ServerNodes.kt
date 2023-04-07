package org.anvilpowered.anvil.entity

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

object ServerNodes : AnvilTable("server_nodes") {
    val name = varchar("name", 255).uniqueIndex()
    val gameTypeId = reference("game_type_id", GameTypes)
}

context(ServerNodes)
fun InsertStatement<*>.setValuesFrom(serverNode: ServerNode.CreateDto) {
    this[name] = serverNode.name
    this[gameTypeId] = serverNode.gameTypeId
}

fun ResultRow.toServerNode() = ServerNode(
    name = this[ServerNodes.name],
    gameType = toGameType(),
    id = this[ServerNodes.id].value,
)
