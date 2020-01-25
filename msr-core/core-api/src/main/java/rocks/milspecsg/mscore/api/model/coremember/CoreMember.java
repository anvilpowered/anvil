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

package rocks.milspecsg.mscore.api.model.coremember;

import rocks.milspecsg.msrepository.api.model.ObjectWithId;

import java.time.Instant;
import java.util.UUID;

public interface CoreMember<TKey> extends ObjectWithId<TKey> {

    UUID getUserUUID();
    void setUserUUID(UUID userUUID);

    String getUserName();
    void setUserName(String userName);

    String getIpAddress();
    void setIpAddress(String ipAddress);

    Instant getLastJoinedUtc();
    void setLastJoinedUtc(Instant joinedUtc);

    String getNickName();
    void setNickName(String nickName);

    boolean isBanned();
    void setBanned(boolean banned);

    boolean isMuted();
    void setMuted(boolean muted);

    Instant getBanEndUtc();
    void setBanEndUtc(Instant banEndUtc);

    Instant getMuteEndUtc();
    void setMuteEndUtc(Instant muteEndUtc);

    String getBanReason();
    void setBanReason(String banReason);

    String getMuteReason();
    void setMuteReason(String muteReason);
}
