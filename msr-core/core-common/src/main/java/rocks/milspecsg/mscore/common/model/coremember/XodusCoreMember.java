/*
 *   MSRepository - MilSpecSG
 *   Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.mscore.common.model.coremember;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import rocks.milspecsg.mscore.api.model.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.datastore.annotation.XodusEntity;
import rocks.milspecsg.msrepository.common.model.XodusDbo;

import java.util.Date;
import java.util.UUID;

@XodusEntity
public class XodusCoreMember extends XodusDbo implements CoreMember<EntityId> {

    private String userUUID;
    private String userName;
    private String ipAddress;
    private long lastJoinedUtc;

    @Override
    public UUID getUserUUID() {
        return UUID.fromString(userUUID);
    }

    @Override
    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID.toString();
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public Date getLastJoinedUtc() {
        return new Date(lastJoinedUtc);
    }

    @Override
    public void setLastJoinedUtc(Date lastJoinedUtc) {
        this.lastJoinedUtc = lastJoinedUtc.getTime();
    }

    @Override
    public Entity writeTo(Entity object) {
        super.writeTo(object);
        if (userUUID != null) {
            object.setProperty("userUUID", userUUID);
        }
        if (userName != null) {
            object.setProperty("userName", userName);
        }
        if (ipAddress != null) {
            object.setProperty("ipAddress", ipAddress);
        }
        object.setProperty("lastJoinedUtc", lastJoinedUtc);
        return object;
    }

    @Override
    public void readFrom(Entity object) {
        super.readFrom(object);
        Comparable<?> userUUID = object.getProperty("userUUID");
        if (userUUID instanceof String) {
            this.userUUID = (String) userUUID;
        }
        Comparable<?> userName = object.getProperty("userName");
        if (userName instanceof String) {
            this.userName = (String) userName;
        }
        Comparable<?> ipAddress = object.getProperty("ipAddress");
        if (ipAddress instanceof String) {
            this.ipAddress = (String) ipAddress;
        }
        Comparable<?> lastJoinedUtc = object.getProperty("lastJoinedUtc");
        if (lastJoinedUtc instanceof Long) {
            this.lastJoinedUtc = (Long) lastJoinedUtc;
        }
    }
}
