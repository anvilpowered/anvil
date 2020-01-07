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

package rocks.milspecsg.msrepository.model.data.dbo;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;

import java.util.Date;

public abstract class XodusDbo implements ObjectWithId<EntityId>, Mappable<Entity> {

    private EntityId id;

    private long createdUtc;
    private long updatedUtc;

    protected XodusDbo() {
        createdUtc = new Date().getTime();
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
    public int getCreatedUtcTimeStampSeconds() {
        return (int) (createdUtc / 1000);
    }

    @Override
    public int getUpdatedUtcTimeStampSeconds() {
        return (int) (updatedUtc / 1000);
    }

    @Override
    public long getCreatedUtcTimeStampMillis() {
        return createdUtc;
    }

    @Override
    public long getUpdatedUtcTimeStampMillis() {
        return updatedUtc;
    }

    @Override
    public Date getCreatedUtcDate() {
        return new Date(createdUtc);
    }

    @Override
    public Date getUpdatedUtcDate() {
        return new Date(updatedUtc);
    }

    protected void prePersist() {
        updatedUtc = new Date().getTime();
    }

    @Override
    public Entity writeTo(Entity object) {
        // id cannot be written to object
        object.setProperty("createdUtc", createdUtc);
        object.setProperty("updatedUtc", updatedUtc);
        return object;
    }

    @Override
    public void readFrom(Entity object) {
        id = object.getId();
        Comparable<?> createdUtc = object.getProperty("createdUtc");
        if (createdUtc instanceof Long) {
            this.createdUtc = (Long) createdUtc;
        }
        Comparable<?> updatedUtc = object.getProperty("updatedUtc");
        if (updatedUtc instanceof Long) {
            this.updatedUtc = (Long) updatedUtc;
        }
    }
}
