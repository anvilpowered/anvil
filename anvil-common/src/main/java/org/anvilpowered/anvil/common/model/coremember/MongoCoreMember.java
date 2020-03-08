/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
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

package org.anvilpowered.anvil.common.model.coremember;

import org.anvilpowered.anvil.base.model.MongoDbo;
import org.anvilpowered.anvil.api.core.model.coremember.CoreMember;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity("coreMembers")
public class MongoCoreMember extends MongoDbo implements CoreMember<ObjectId> {

    private UUID userUUID;
    private String userName;
    private BigDecimal balance;
    private String ipAddress;
    private Instant lastJoinedUtc;
    private String nickName;
    private boolean banned;
    private boolean muted;
    private Instant banEndUtc;
    private Instant muteEndUtc;
    private String banReason;
    private String muteReason;

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
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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
    public Instant getLastJoinedUtc() {
        return lastJoinedUtc;
    }

    @Override
    public void setLastJoinedUtc(Instant lastJoinedUtc) {
        this.lastJoinedUtc = lastJoinedUtc;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public boolean isBanned() {
        return banned;
    }

    @Override
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    @Override
    public Instant getBanEndUtc() {
        return banEndUtc;
    }

    @Override
    public void setBanEndUtc(Instant banEndUtc) {
        this.banEndUtc = banEndUtc;
    }

    @Override
    public Instant getMuteEndUtc() {
        return muteEndUtc;
    }

    @Override
    public void setMuteEndUtc(Instant muteEndUtc) {
        this.muteEndUtc = muteEndUtc;
    }

    @Override
    public String getBanReason() {
        return banReason;
    }

    @Override
    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    @Override
    public String getMuteReason() {
        return muteReason;
    }

    @Override
    public void setMuteReason(String muteReason) {
        this.muteReason = muteReason;
    }
}
