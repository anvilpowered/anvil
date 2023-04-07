package org.anvilpowered.anvil.entity

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

object GameTypes : AnvilTable("game_types") {
    val name = varchar("name", 255).uniqueIndex()
    val website = varchar("website", 255)
}

context(GameTypes)
fun InsertStatement<*>.setValuesFrom(gameType: GameType.CreateDto) {
    this[name] = gameType.name
    this[website] = gameType.website
}

fun ResultRow.toGameType() = GameType(
    name = this[GameTypes.name],
    website = this[GameTypes.website],
    id = this[GameTypes.id].value,
)
