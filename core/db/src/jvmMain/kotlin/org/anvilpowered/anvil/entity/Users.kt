package org.anvilpowered.anvil.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object Users : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val minecraftUsername = varchar("minecraftUsername", 16).uniqueIndex()
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users, User::class.java, ::User)
    var email by Users.email
    var minecraftUsername by Users.minecraftUsername
}
