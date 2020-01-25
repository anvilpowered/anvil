/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.common.model;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import rocks.milspecsg.msrepository.api.model.Mappable;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public abstract class XodusDbo implements ObjectWithId<EntityId>, Mappable<Entity> {

    private EntityId id;

    private long createdUtcSeconds;
    private int createdUtcNanos;
    private long updatedUtcSeconds;
    private int updatedUtcNanos;

    protected XodusDbo() {
        Instant now = OffsetDateTime.now(ZoneOffset.UTC).toInstant();
        createdUtcSeconds = now.getEpochSecond();
        createdUtcNanos = now.getNano();
        prePersist();
    }

    @Override
    public EntityId getId() {
        return id;
    }

    @Override
    public void setId(EntityId id) {
        this.id = id;
    }

    @Override
    public String getIdAsString() {
        return id.toString();
    }

    @Override
    public Instant getCreatedUtc() {
        return Instant.ofEpochSecond(createdUtcSeconds, createdUtcNanos);
    }

    @Override
    public Instant getUpdatedUtc() {
        return Instant.ofEpochSecond(updatedUtcSeconds, updatedUtcNanos);
    }

    protected void prePersist() {
        Instant now = OffsetDateTime.now(ZoneOffset.UTC).toInstant();
        updatedUtcSeconds = now.getEpochSecond();
        updatedUtcNanos = now.getNano();
    }

    @Override
    public Entity writeTo(Entity object) {
        // id cannot be written to object
        object.setProperty("createdUtcSeconds", createdUtcSeconds);
        object.setProperty("createdUtcNanos", createdUtcNanos);
        object.setProperty("updatedUtcSeconds", updatedUtcSeconds);
        object.setProperty("updatedUtcNanos", updatedUtcNanos);
        return object;
    }

    @Override
    public void readFrom(Entity object) {
        id = object.getId();
        Comparable<?> createdUtcSeconds = object.getProperty("createdUtcSeconds");
        if (createdUtcSeconds instanceof Long) {
            this.createdUtcSeconds = (Long) createdUtcSeconds;
        }
        Comparable<?> createdUtcNanos = object.getProperty("createdUtcNanos");
        if (createdUtcNanos instanceof Integer) {
            this.createdUtcNanos = (Integer) createdUtcNanos;
        }
        Comparable<?> updatedUtcSeconds = object.getProperty("updatedUtcSeconds");
        if (updatedUtcSeconds instanceof Long) {
            this.updatedUtcSeconds = (Long) updatedUtcSeconds;
        }
        Comparable<?> updatedUtcNanos = object.getProperty("updatedUtcNanos");
        if (updatedUtcNanos instanceof Integer) {
            this.updatedUtcNanos = (Integer) updatedUtcNanos;
        }
    }
}
