/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.anvilpowered.anvil.base.model

import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityId
import org.anvilpowered.anvil.api.model.Mappable
import org.anvilpowered.anvil.api.model.ObjectWithId
import java.lang.IllegalStateException
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

abstract class XodusDbo protected constructor() : ObjectWithId<EntityId>, Mappable<Entity> {

    private var id: EntityId? = null
    private var createdUtcSeconds: Long
    private var createdUtcNanos: Int
    private var updatedUtcSeconds: Long = 0
    private var updatedUtcNanos = 0

    init {
        val now = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
        createdUtcSeconds = now.epochSecond
        createdUtcNanos = now.nano
        prePersist()
    }

    override fun getId(): EntityId {
        return id ?: throw IllegalStateException("EntityId may not be null!")
    }

    override fun setId(id: EntityId) {
        this.id = id
    }

    override var idAsString: String = id.toString()
    override val createdUtc: Instant = Instant.ofEpochSecond(createdUtcSeconds, createdUtcNanos.toLong())
    override var updatedUtc: Instant = Instant.ofEpochSecond(updatedUtcSeconds, updatedUtcNanos.toLong())

    private fun prePersist() {
        val now = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
        updatedUtcSeconds = now.epochSecond
        updatedUtcNanos = now.nano
    }

    override fun writeTo(obj: Entity): Entity {
        // id cannot be written to object
        obj.setProperty("createdUtcSeconds", createdUtcSeconds)
        obj.setProperty("createdUtcNanos", createdUtcNanos)
        obj.setProperty("updatedUtcSeconds", updatedUtcSeconds)
        obj.setProperty("updatedUtcNanos", updatedUtcNanos)
        return obj
    }

    override fun readFrom(obj: Entity) {
        id = obj.id
        val createdUtcSeconds = obj.getProperty("createdUtcSeconds")
        if (createdUtcSeconds is Long) {
            this.createdUtcSeconds = createdUtcSeconds
        }
        val createdUtcNanos = obj.getProperty("createdUtcNanos")
        if (createdUtcNanos is Int) {
            this.createdUtcNanos = createdUtcNanos
        }
        val updatedUtcSeconds = obj.getProperty("updatedUtcSeconds")
        if (updatedUtcSeconds is Long) {
            this.updatedUtcSeconds = updatedUtcSeconds
        }
        val updatedUtcNanos = obj.getProperty("updatedUtcNanos")
        if (updatedUtcNanos is Int) {
            this.updatedUtcNanos = updatedUtcNanos
        }
    }
}
