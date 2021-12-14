/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.common.coremember;

import com.google.inject.Inject;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import org.anvilpowered.anvil.api.coremember.XodusCoreMemberRepository;
import org.anvilpowered.anvil.api.model.Mappable;
import org.anvilpowered.anvil.api.model.coremember.CoreMember;
import org.anvilpowered.anvil.base.datastore.BaseXodusRepository;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CommonXodusCoreMemberRepository
    extends CommonCoreMemberRepository<EntityId, PersistentEntityStore>
    implements BaseXodusRepository<CoreMember<EntityId>>,
    XodusCoreMemberRepository {

    @Inject
    private Logger logger;

    @Override
    public CompletableFuture<Optional<CoreMember<EntityId>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress, boolean[] flags) {
        final int length = flags.length;
        if (length != 8) throw new IllegalArgumentException("Flags must be an array of length 8");
        for (int i = 0; i < length; i++) {
            flags[i] = false;
        }
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().computeInTransaction(txn -> {
                Iterator<Entity> iterator = asQuery(userUUID).apply(txn).iterator();
                if (!iterator.hasNext()) {
                    CoreMember<EntityId> member = generateEmpty();
                    member.setUserUUID(userUUID);
                    member.setUserName(userName);
                    member.setIpAddress(ipAddress);
                    member.setLastJoinedUtc(OffsetDateTime.now(ZoneOffset.UTC).toInstant());
                    flags[0] = true;
                    return insertOne(member).join();
                }

                CoreMember<EntityId> member = generateEmpty();
                Entity entity = iterator.next();
                ((Mappable<Entity>) member).readFrom(entity);

                boolean updateUsername = false;
                boolean updateIPAddress = false;

                if (!userName.equals(member.getUserName())) {
                    entity.setProperty("userName", userName);
                    updateUsername = true;
                }

                if (!ipAddress.equals(member.getIpAddress())) {
                    entity.setProperty("ipAddress", ipAddress);
                    updateIPAddress = true;
                }
                Instant now = OffsetDateTime.now(ZoneOffset.UTC).toInstant();
                long nowSeconds = now.getEpochSecond();
                int nowNanos = now.getNano();
                entity.setProperty("lastJoinedUtcSeconds", nowSeconds);
                entity.setProperty("lastJoinedUtcNanos", nowNanos);
                entity.setProperty("updatedUtcSeconds", nowSeconds);
                entity.setProperty("updatedUtcNanos", nowNanos);
                if (txn.commit()) {
                    if (updateUsername) {
                        member.setUserName(userName);
                        flags[1] = true;
                    }
                    if (updateIPAddress) {
                        member.setIpAddress(ipAddress);
                        flags[2] = true;
                    }
                    member.setLastJoinedUtc(now);
                    return Optional.of(member);
                }
                logger.error("Failed to update {} please report this on github!", userName);
                return Optional.empty();
            })
        );
    }

    @Override
    public CompletableFuture<Optional<CoreMember<EntityId>>> getOneForUser(UUID userUUID) {
        return getOne(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<Optional<CoreMember<EntityId>>> getOneForUser(String userName) {
        return getOne(asQuery(userName));
    }

    @Override
    public CompletableFuture<List<CoreMember<EntityId>>> getForIpAddress(String ipAddress) {
        return getAll(asQueryForIpAddress(ipAddress));
    }

    @Override
    public CompletableFuture<Boolean> ban(EntityId id, Instant endUtc, String reason) {
        return ban(asQuery(id), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> banUser(UUID userUUID, Instant endUtc, String reason) {
        return ban(asQuery(userUUID), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> banUser(String userName, Instant endUtc, String reason) {
        return ban(asQuery(userName), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> banIpAddress(String ipAddress, Instant endUtc, String reason) {
        return ban(asQueryForIpAddress(ipAddress), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> unBan(EntityId id) {
        return unBan(asQuery(id));
    }

    @Override
    public CompletableFuture<Boolean> unBanUser(UUID userUUID) {
        return unBan(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<Boolean> unBanUser(String userName) {
        return unBan(asQuery(userName));
    }

    @Override
    public CompletableFuture<Boolean> unBanIpAddress(String ipAddress) {
        return unBan(asQueryForIpAddress(ipAddress));
    }

    @Override
    public CompletableFuture<Boolean> mute(EntityId id, Instant endUtc, String reason) {
        return mute(asQuery(id), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> muteUser(UUID userUUID, Instant endUtc, String reason) {
        return mute(asQuery(userUUID), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> muteUser(String userName, Instant endUtc, String reason) {
        return mute(asQuery(userName), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> muteIpAddress(String ipAddress, Instant endUtc, String reason) {
        return mute(asQueryForIpAddress(ipAddress), endUtc, reason);
    }

    @Override
    public CompletableFuture<Boolean> unMute(EntityId id) {
        return unMute(asQuery(id));
    }

    @Override
    public CompletableFuture<Boolean> unMuteUser(UUID userUUID) {
        return unMute(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<Boolean> unMuteUser(String userName) {
        return unMute(asQuery(userName));
    }

    @Override
    public CompletableFuture<Boolean> unMuteIpAddress(String ipAddress) {
        return unMute(asQueryForIpAddress(ipAddress));
    }

    @Override
    public CompletableFuture<Boolean> setNickName(EntityId id, String nickName) {
        return setNickName(asQuery(id), nickName);
    }

    @Override
    public CompletableFuture<Boolean> setNickNameForUser(UUID userUUID, String nickName) {
        return setNickName(asQuery(userUUID), nickName);
    }

    @Override
    public CompletableFuture<Boolean> setNickNameForUser(String userName, String nickName) {
        return setNickName(asQuery(userName), nickName);
    }

    @Override
    public CompletableFuture<Boolean> deleteNickName(EntityId id) {
        return deleteNickName(asQuery(id));
    }

    @Override
    public CompletableFuture<Boolean> deleteNickNameForUser(UUID userUUID) {
        return deleteNickName(asQuery(userUUID));
    }

    @Override
    public CompletableFuture<Boolean> deleteNickNameForUser(String userName) {
        return deleteNickName(asQuery(userName));
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(UUID userUUID) {
        return txn -> txn.find(getTClass().getSimpleName(), "userUUID", userUUID.toString());
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(String userName) {
        return txn -> txn.find(getTClass().getSimpleName(), "userName", userName);
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForIpAddress(String ipAddress) {
        return txn -> txn.find(getTClass().getSimpleName(), "ipAddress", ipAddress);
    }

    @Override
    public CompletableFuture<Boolean> ban(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant endUtc, String reason) {
        return update(query, e -> {
            e.setProperty("banned", true);
            e.setProperty("banEndUtcSeconds", endUtc.getEpochSecond());
            e.setProperty("banEndUtcNanos", endUtc.getNano());
            e.setProperty("banReason", reason);
        });
    }

    @Override
    public CompletableFuture<Boolean> unBan(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return update(query, e -> e.setProperty("banned", false));
    }

    @Override
    public CompletableFuture<Boolean> mute(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant endUtc, String reason) {
        return update(query, e -> {
            e.setProperty("muted", true);
            e.setProperty("muteEndUtcSeconds", endUtc.getEpochSecond());
            e.setProperty("muteEndUtcNanos", endUtc.getNano());
            e.setProperty("muteReason", reason);
        });
    }

    @Override
    public CompletableFuture<Boolean> unMute(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return update(query, e -> e.setProperty("muted", false));
    }

    @Override
    public CompletableFuture<Boolean> setNickName(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, String nickName) {
        return update(query, e -> e.setProperty("nickName", nickName));
    }

    @Override
    public CompletableFuture<Boolean> deleteNickName(Function<? super StoreTransaction, ? extends Iterable<Entity>> query) {
        return update(query, e -> e.deleteProperty("nickName"));
    }
}
