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

import dev.morphia.annotations.Id
import dev.morphia.annotations.PrePersist
import org.anvilpowered.anvil.api.model.ObjectWithId
import org.bson.types.ObjectId
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

abstract class MongoDbo : ObjectWithId<ObjectId> {

    @Id
    private lateinit var id: ObjectId
    override lateinit var updatedUtc: Instant

    override fun setId(id: ObjectId) {
        this.id = id
    }

    override var idAsString: String = id.toHexString()
    override val createdUtc: Instant = Instant.ofEpochSecond(id.timestamp.toLong())

    @PrePersist
    private fun prePersist() {
        updatedUtc = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
    }
}
