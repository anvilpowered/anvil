package org.anvilpowered.anvil.entity

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

object MinecraftUsers : AnvilTable("minecraft_users") {
    val username = varchar("name", 255).uniqueIndex()
    val userId = reference("user_id", Users)
}

context(MinecraftUsers)
fun InsertStatement<*>.setValuesFrom(minecraftUser: MinecraftUser.CreateDto) {
    this[username] = minecraftUser.username
    this[userId] = minecraftUser.userId
    this[id] = minecraftUser.id
}

fun ResultRow.toMinecraftUser() = MinecraftUser(
    username = this[MinecraftUsers.username],
    userId = this[MinecraftUsers.userId].value,
    id = this[MinecraftUsers.id].value,
)
