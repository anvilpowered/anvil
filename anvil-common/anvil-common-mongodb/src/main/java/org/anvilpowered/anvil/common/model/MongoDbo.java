/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.common.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public abstract class MongoDbo implements ObjectWithId<ObjectId> {

    @Id
    private ObjectId id;

    private Instant updatedUtc;

    @Override
    public ObjectId getId() {
        return id;
    }

    @Override
    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public String getIdAsString() {
        return id.toHexString();
    }

    @Override
    public Instant getCreatedUtc() {
        return Instant.ofEpochSecond(id.getTimestamp());
    }

    @Override
    public Instant getUpdatedUtc() {
        return updatedUtc;
    }

    @PrePersist
    private void prePersist() {
        updatedUtc = OffsetDateTime.now(ZoneOffset.UTC).toInstant();
    }
}

