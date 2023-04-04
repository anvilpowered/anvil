package org.anvilpowered.anvil.entity

import kotlinx.datetime.toJavaInstant
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

object GameTypes : AnvilTable("game_types") {
    val name = varchar("name", 255).uniqueIndex()
    val website = varchar("website", 255)
}

context(GameTypes)
fun InsertStatement<*>.setValuesFrom(gameType: GameType) {
    this[name] = gameType.name
    this[website] = gameType.website
    this[id] = gameType.id
    this[createdUtc] = gameType.createdUtc.toJavaInstant()
    this[updatedUtc] = gameType.updatedUtc.toJavaInstant()
}

fun ResultRow.toGameType(): GameType {
    return GameType(
        name = this[GameTypes.name],
        website = this[GameTypes.website],
        id = this[GameTypes.id].value,
    )
}
