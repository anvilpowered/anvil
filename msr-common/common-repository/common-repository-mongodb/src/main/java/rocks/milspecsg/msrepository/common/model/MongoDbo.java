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

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;

import java.util.Date;

public abstract class MongoDbo implements ObjectWithId<ObjectId> {

    @Id
    private ObjectId id;

    private Date updatedUtc;

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
    public int getCreatedUtcTimeStampSeconds() {
        return id.getTimestamp();
    }

    @Override
    public int getUpdatedUtcTimeStampSeconds() {
        return (int) (updatedUtc.getTime() / 1000);
    }

    @Override
    public long getCreatedUtcTimeStampMillis() {
        return id.getTimestamp() * 1000L;
    }

    @Override
    public long getUpdatedUtcTimeStampMillis() {
        return updatedUtc.getTime();
    }

    @Override
    public Date getCreatedUtcDate() {
        return id.getDate();
    }

    @Override
    public Date getUpdatedUtcDate() {
        return updatedUtc;
    }

    @PrePersist
    private void prePersist() {
        updatedUtc = new Date();
    }
}

