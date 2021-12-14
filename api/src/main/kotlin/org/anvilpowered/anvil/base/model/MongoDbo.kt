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
import org.anvilpowered.anvil.api.model.ObjectWithId

abstract class MongoDbo : ObjectWithId<ObjectId?> {
    @Id
    private var id: ObjectId? = null
    private var updatedUtc: Instant? = null
    override fun getId(): ObjectId {
        return id
    }

    override fun setId(id: ObjectId) {
        this.id = id
    }

    val idAsString: String
        get() = id.toHexString()
    val createdUtc: Instant
        get() = Instant.ofEpochSecond(id.getTimestamp())

    override fun getUpdatedUtc(): Instant? {
        return updatedUtc
    }

    @PrePersist
    private fun prePersist() {
        updatedUtc = OffsetDateTime.now(ZoneOffset.UTC).toInstant()
    }
}
