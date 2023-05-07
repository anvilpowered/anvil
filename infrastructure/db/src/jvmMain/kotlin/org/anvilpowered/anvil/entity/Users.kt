package org.anvilpowered.anvil.entity

import org.anvilpowered.anvil.domain.entity.AnvilUser
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.InsertStatement

object Users : UUIDTable("users") {
    val username = varchar("username", 255).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
}

context(Users)
fun InsertStatement<*>.setValuesFrom(user: AnvilUser.CreateDto) {
    this[username] = user.username
    this[email] = user.email
}

fun ResultRow.toUser() = AnvilUser(
    username = this[Users.username],
    email = this[Users.email],
    id = this[Users.id].value,
)
