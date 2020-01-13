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

package rocks.milspecsg.mscore.service.common.coremember.repository;

import com.google.inject.Inject;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import rocks.milspecsg.mscore.api.coremember.repository.XodusCoreMemberRepository;
import rocks.milspecsg.mscore.model.core.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.Mappable;
import rocks.milspecsg.msrepository.service.common.repository.CommonXodusRepository;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CommonXodusCoreMemberRepository
    extends CommonCoreMemberRepository<EntityId, PersistentEntityStore>
    implements CommonXodusRepository<CoreMember<EntityId>, CacheService<EntityId, CoreMember<EntityId>, PersistentEntityStore>>,
    XodusCoreMemberRepository {

    @Inject
    protected CommonXodusCoreMemberRepository(DataStoreContext<EntityId, PersistentEntityStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<CoreMember<EntityId>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress, boolean[] flags) {
        final int length = flags.length;
        if (length != 8) throw new IllegalArgumentException("Flags must be an array of length 8");
        for (int i = 0; i < length; i++) {
            flags[i] = false;
        }
        return CompletableFuture.supplyAsync(() ->
            getDataStoreContext().getDataStore().flatMap(dataStore ->
                dataStore.computeInTransaction(txn -> {
                    Iterator<Entity> iterator = asQueryForUser(userUUID).apply(txn).iterator();
                    if (!iterator.hasNext()) {
                        CoreMember<EntityId> member = generateEmpty();
                        member.setUserUUID(userUUID);
                        member.setUserName(userName);
                        member.setIpAddress(ipAddress);
                        member.setLastJoinedUtc(new Date());
                        flags[0] = true;
                        return insertOne(member).join();
                    }

                    CoreMember<EntityId> item = generateEmpty();
                    Entity entity = iterator.next();
                    ((Mappable<Entity>) item).readFrom(entity);

                    boolean updateUsername = false;
                    boolean updateIPAddress = false;

                    if (!item.getUserName().equals(userName)) {
                        entity.setProperty("userName", userName);
                        updateUsername = true;
                    }

                    if (!item.getIpAddress().equals(ipAddress)) {
                        entity.setProperty("ipAddress", ipAddress);
                        updateIPAddress = true;
                    }
                    Date date = new Date();
                    long time = date.getTime();
                    entity.setProperty("lastJoinedUtc", time);
                    entity.setProperty("updatedUtc", time);
                    if (txn.commit()) {
                        if (updateUsername) {
                            item.setUserName(userName);
                            flags[1] = true;
                        }
                        if (updateIPAddress) {
                            item.setIpAddress(ipAddress);
                            flags[2] = true;
                        }
                        item.setLastJoinedUtc(date);
                        return Optional.of(item);
                    }
                    System.err.println("Failed to update " + userName + " please report this on github!");
                    return Optional.empty();
                })
            )
        );
    }

    @Override
    public CompletableFuture<Optional<CoreMember<EntityId>>> getOneForUser(UUID userUUID) {
        return getOne(asQueryForUser(userUUID));
    }

    @Override
    public CompletableFuture<Optional<CoreMember<EntityId>>> getOneForUser(String userName) {
        return getOne(asQueryForUser(userName));
    }

    @Override
    public CompletableFuture<List<CoreMember<EntityId>>> getForIpAddress(String ipAddress) {
        return getAll(asQueryForIpAddress(ipAddress));
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForUser(UUID userUUID) {
        return txn -> txn.find(getTClass().getSimpleName(), "userUUID", userUUID.toString());
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForUser(String userName) {
        return txn -> txn.find(getTClass().getSimpleName(), "userName", userName);
    }

    @Override
    public Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForIpAddress(String ipAddress) {
        return txn -> txn.find(getTClass().getSimpleName(), "ipAddress", ipAddress);
    }
}
