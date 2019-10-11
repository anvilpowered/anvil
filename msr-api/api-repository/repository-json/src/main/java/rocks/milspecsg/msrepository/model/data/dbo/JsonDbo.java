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

import io.jsondb.annotation.Id;
import org.bson.types.ObjectId;

import java.util.Date;

public abstract class JsonDbo implements ObjectWithId<ObjectId> {

    @Id
    private ObjectId id;

    private Date updatedUtc;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
        prePersist();
    }

    public Date getUpdatedUtc() {
        return updatedUtc;
    }

    protected void prePersist() {
        updatedUtc = new Date();
    }
}
