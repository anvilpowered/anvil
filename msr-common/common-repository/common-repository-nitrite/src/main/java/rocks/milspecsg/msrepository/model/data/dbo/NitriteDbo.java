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

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.Date;

public abstract class NitriteDbo implements ObjectWithId<NitriteId>, Mappable, Serializable {

    @Id
    private long id;

    private Date createdUtc;
    private Date updatedUtc;

    @Override
    public NitriteId getId() {
        return NitriteId.createId(id);
    }

    @Override
    public void setId(NitriteId id) {
        this.id = id.getIdValue();
        createdUtc = new Date();
        prePersist();
    }

    @Override
    public String getIdAsString() {
        return getId().toString();
    }

    @Override
    public int getCreatedUtcTimeStampSeconds() {
        return (int) (createdUtc.getTime() / 1000);
    }

    @Override
    public int getUpdatedUtcTimeStampSeconds() {
        return (int) (updatedUtc.getTime() / 1000);
    }

    @Override
    public long getCreatedUtcTimeStampMillis() {
        return createdUtc.getTime();
    }

    @Override
    public long getUpdatedUtcTimeStampMillis() {
        return updatedUtc.getTime();
    }

    @Override
    public Date getCreatedUtcDate() {
        return createdUtc;
    }

    @Override
    public Date getUpdatedUtcDate() {
        return updatedUtc;
    }

    protected void prePersist() {
        updatedUtc = new Date();
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = new Document();
        document.put("_id", id);
        document.put("createdUtc", createdUtc);
        document.put("updatedUtc", updatedUtc);
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        id = document.getId().getIdValue();
        createdUtc = (Date) document.get("createdUtc");
        updatedUtc = (Date) document.get("updatedUtc");
    }
}
