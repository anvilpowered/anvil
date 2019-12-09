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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsondb.annotation.Id;

import java.util.Date;
import java.util.UUID;

public abstract class JsonDbo implements ObjectWithId<UUID> {

    @Id
    private UUID id;

    private long createdUtc;
    private long updatedUtc;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
        createdUtc = new Date().getTime();
        prePersist();
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
        prePersist();
    }

    public long getUpdatedUtc() {
        return updatedUtc;
    }

    public void setUpdatedUtc(long updatedUtc) {
        this.updatedUtc = updatedUtc;
        prePersist();
    }

    @Override
    @JsonIgnore
    public String getIdAsString() {
        return id.toString();
    }

    @Override
    @JsonIgnore
    public int getCreatedUtcTimeStampSeconds() {
        return (int) (createdUtc / 1000);
    }

    @Override
    @JsonIgnore
    public int getUpdatedUtcTimeStampSeconds() {
        return (int) (updatedUtc / 1000);
    }

    @Override
    @JsonIgnore
    public long getCreatedUtcTimeStampMillis() {
        return createdUtc;
    }

    @Override
    @JsonIgnore
    public long getUpdatedUtcTimeStampMillis() {
        return updatedUtc;
    }

    @Override
    @JsonIgnore
    public Date getCreatedUtcDate() {
        return new Date(createdUtc);
    }

    @Override
    @JsonIgnore
    public Date getUpdatedUtcDate() {
        return new Date(updatedUtc);
    }

    @JsonIgnore
    protected void prePersist() {
        updatedUtc = new Date().getTime();
    }
}
