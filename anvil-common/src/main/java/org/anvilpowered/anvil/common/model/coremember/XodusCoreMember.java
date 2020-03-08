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

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import org.anvilpowered.anvil.api.datastore.annotation.XodusEntity;
import org.anvilpowered.anvil.base.model.XodusDbo;
import org.anvilpowered.anvil.api.core.model.coremember.CoreMember;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@XodusEntity
public class XodusCoreMember extends XodusDbo implements CoreMember<EntityId> {

    private String userUUID;
    private String userName;
    private String balance;
    private String ipAddress;
    private long lastJoinedUtcSeconds;
    private int lastJoinedUtcNanos;
    private String nickName;
    private boolean banned;
    private boolean muted;
    private long banEndUtcSeconds;
    private int banEndUtcNanos;
    private long muteEndUtcSeconds;
    private int muteEndUtcNanos;
    private String banReason;
    private String muteReason;

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
    public BigDecimal getBalance() {
        return new BigDecimal(balance);
    }

    @Override
    public void setBalance(BigDecimal balance) {
        this.balance = balance.toString();
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
        return Instant.ofEpochSecond(lastJoinedUtcSeconds, lastJoinedUtcNanos);
    }

    @Override
    public void setLastJoinedUtc(Instant lastJoinedUtc) {
        lastJoinedUtcSeconds = lastJoinedUtc.getEpochSecond();
        lastJoinedUtcNanos = lastJoinedUtc.getNano();
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
        return Instant.ofEpochSecond(banEndUtcSeconds, banEndUtcNanos);
    }

    @Override
    public void setBanEndUtc(Instant banEndUtc) {
        banEndUtcSeconds = banEndUtc.getEpochSecond();
        banEndUtcNanos = banEndUtc.getNano();
    }

    @Override
    public Instant getMuteEndUtc() {
        return Instant.ofEpochSecond(muteEndUtcSeconds, muteEndUtcNanos);
    }

    @Override
    public void setMuteEndUtc(Instant muteEndUtc) {
        muteEndUtcSeconds = muteEndUtc.getEpochSecond();
        muteEndUtcNanos = muteEndUtc.getNano();
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

    @Override
    public Entity writeTo(Entity object) {
        super.writeTo(object);
        if (userUUID != null) {
            object.setProperty("userUUID", userUUID);
        }
        if (userName != null) {
            object.setProperty("userName", userName);
        }
        if (balance != null) {
            object.setProperty("balance", balance);
        }
        if (ipAddress != null) {
            object.setProperty("ipAddress", ipAddress);
        }
        object.setProperty("lastJoinedUtcSeconds", lastJoinedUtcSeconds);
        object.setProperty("lastJoinedUtcNanos", lastJoinedUtcNanos);
        if (nickName != null) {
            object.setProperty("nickname", nickName);
        }
        object.setProperty("banned", banned);
        object.setProperty("muted", muted);
        object.setProperty("banEndUtcSeconds", banEndUtcSeconds);
        object.setProperty("banEndUtcNanos", banEndUtcNanos);
        object.setProperty("muteEndUtcSeconds", muteEndUtcSeconds);
        object.setProperty("muteEndUtcNanos", muteEndUtcNanos);
        if (banReason != null) {
            object.setProperty("banReason", banReason);
        }
        if (muteReason != null) {
            object.setProperty("muteReason", muteReason);
        }
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
        Comparable<?> balance = object.getProperty("balance");
        if (balance instanceof String) {
            this.balance = (String) balance;
        }
        Comparable<?> ipAddress = object.getProperty("ipAddress");
        if (ipAddress instanceof String) {
            this.ipAddress = (String) ipAddress;
        }
        Comparable<?> lastJoinedUtcSeconds = object.getProperty("lastJoinedUtcSeconds");
        if (lastJoinedUtcSeconds instanceof Long) {
            this.lastJoinedUtcSeconds = (Long) lastJoinedUtcSeconds;
        }
        Comparable<?> lastJoinedUtcNanos = object.getProperty("lastJoinedUtcNanos");
        if (lastJoinedUtcNanos instanceof Integer) {
            this.lastJoinedUtcNanos = (Integer) lastJoinedUtcNanos;
        }
        Comparable<?> nickName = object.getProperty("nickName");
        if (nickName instanceof String) {
            this.nickName = (String) nickName;
        }
        Comparable<?> banned = object.getProperty("banned");
        if (banned instanceof Boolean) {
            this.banned = (Boolean) banned;
        }
        Comparable<?> muted = object.getProperty("muted");
        if (muted instanceof Boolean) {
            this.muted = (Boolean) muted;
        }
        Comparable<?> banEndUtcSeconds = object.getProperty("banEndUtcSeconds");
        if (banEndUtcSeconds instanceof Long) {
            this.banEndUtcSeconds = (Long) banEndUtcSeconds;
        }
        Comparable<?> banEndUtcNanos = object.getProperty("banEndUtcNanos");
        if (banEndUtcNanos instanceof Integer) {
            this.banEndUtcNanos = (Integer) banEndUtcNanos;
        }
        Comparable<?> muteEndUtcSeconds = object.getProperty("muteEndUtcSeconds");
        if (muteEndUtcSeconds instanceof Long) {
            this.muteEndUtcSeconds = (Long) muteEndUtcSeconds;
        }
        Comparable<?> muteEndUtcNanos = object.getProperty("muteEndUtcNanos");
        if (muteEndUtcNanos instanceof Integer) {
            this.muteEndUtcNanos = (Integer) muteEndUtcNanos;
        }
        Comparable<?> banReason = object.getProperty("banReason");
        if (banReason instanceof String) {
            this.banReason = (String) banReason;
        }
        Comparable<?> muteReason = object.getProperty("muteReason");
        if (muteReason instanceof String) {
            this.muteReason = (String) muteReason;
        }
    }
}
