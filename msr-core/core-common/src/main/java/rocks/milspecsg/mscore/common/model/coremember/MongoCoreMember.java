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

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import rocks.milspecsg.mscore.api.model.coremember.CoreMember;
import rocks.milspecsg.msrepository.common.model.MongoDbo;

import java.util.Date;
import java.util.UUID;

@Entity("coreMembers")
public class MongoCoreMember extends MongoDbo implements CoreMember<ObjectId> {

    private UUID userUUID;
    private String userName;
    private String ipAddress;
    private Date lastJoinedUtc;

    @Override
    public UUID getUserUUID() {
        return userUUID;
    }

    @Override
    public void setUserUUID(UUID userUUID) {
        this.userUUID = userUUID;
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
        return lastJoinedUtc;
    }

    @Override
    public void setLastJoinedUtc(Date lastJoinedUtc) {
        this.lastJoinedUtc = lastJoinedUtc;
    }
}
