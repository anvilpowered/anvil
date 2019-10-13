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

public abstract class JsonDbo implements ObjectWithId<String> {

    @Id
    private String id;

    private Date createdUtc;
    private Date updatedUtc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.createdUtc = new Date();
        prePersist();
    }

    @Override
    @JsonIgnore
    public String getIdAsString() {
        return id;
    }

    @Override
    public Date getCreatedUtc() {
        return createdUtc;
    }

    @Override
    public Date getUpdatedUtc() {
        return updatedUtc;
    }

    @JsonIgnore
    protected void prePersist() {
        updatedUtc = new Date();
    }
}
