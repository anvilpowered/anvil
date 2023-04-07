package org.anvilpowered.anvil.entity

import org.jetbrains.exposed.dao.id.UUIDTable

abstract class AnvilTable(name: String) : UUIDTable(name) {
//    val createdUtc = timestamp("created_utc")
//    val updatedUtc = timestamp("updated_utc") // TODO: Make sure postgres updates this field
}
