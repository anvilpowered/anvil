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
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.mscore.api.coremember.repository.MongoCoreMemberRepository;
import rocks.milspecsg.mscore.model.core.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.service.common.repository.CommonMongoRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonMongoCoreMemberRepository
    extends CommonCoreMemberRepository<ObjectId, Datastore>
    implements CommonMongoRepository<CoreMember<ObjectId>, CacheService<ObjectId, CoreMember<ObjectId>, Datastore>>,
    MongoCoreMemberRepository {

    @Inject
    public CommonMongoCoreMemberRepository(DataStoreContext<ObjectId, Datastore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneOrGenerateForUser(UUID userUUID, String userName, String ipAddress, boolean[] flags) {
        final int length = flags.length;
        if (length != 8) throw new IllegalArgumentException("Flags must be an array of length 8");
        for (int i = 0; i < length; i++) {
            flags[i] = false;
        }
        return CompletableFuture.supplyAsync(() -> {
            Optional<CoreMember<ObjectId>> optionalMember = getOneForUser(userUUID).join();
            if (optionalMember.isPresent()) {
                Optional<UpdateOperations<CoreMember<ObjectId>>> optionalUpdateOperations = createUpdateOperations();
                if (!optionalUpdateOperations.isPresent()) {
                    return Optional.empty();
                }
                UpdateOperations<CoreMember<ObjectId>> updateOperations = optionalUpdateOperations.get();
                boolean updateName = false;
                boolean updateIpAddress = false;
                if (!userName.equals(optionalMember.get().getUserName())) {
                    updateOperations.set("userName", userName);
                    updateName = true;
                }
                if (!optionalMember.get().getIpAddress().equals(ipAddress)) {
                    updateOperations.set("ipAddress", ipAddress);
                    updateIpAddress = true;
                }
                updateOperations.set("lastJoinedUtc", new Date());
                Optional<Query<CoreMember<ObjectId>>> optionalQuery = asQuery(optionalMember.get().getId());
                if (optionalQuery.isPresent()
                    && getDataStoreContext().getDataStore()
                    .map(d -> d.update(optionalQuery.get(), updateOperations).getUpdatedCount() > 0)
                    .orElse(false)
                ) {
                    if (updateName) {
                        optionalMember.get().setUserName(userName);
                    }
                    if (updateIpAddress) {
                        optionalMember.get().setIpAddress(ipAddress);
                    }
                }
                flags[1] = updateName;
                flags[2] = updateIpAddress;
                return optionalMember;
            }
            // if there isn't one already, create a new one
            CoreMember<ObjectId> member = generateEmpty();
            member.setUserUUID(userUUID);
            member.setUserName(userName);
            member.setIpAddress(ipAddress);
            member.setLastJoinedUtc(new Date());
            flags[0] = true;
            return insertOne(member).join();
        });
    }

    @Override
    public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneForUser(UUID userUUID) {
        return CompletableFuture.supplyAsync(() -> asQueryForUser(userUUID).map(QueryResults::get));
    }

    @Override
    public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneForUser(String userName) {
        return CompletableFuture.supplyAsync(() -> asQueryForUser(userName).map(QueryResults::get));
    }

    @Override
    public CompletableFuture<List<CoreMember<ObjectId>>> getForIpAddress(String ipAddress) {
        return CompletableFuture.supplyAsync(() -> asQueryForIpAddress(ipAddress).map(QueryResults::asList).orElse(Collections.emptyList()));
    }

    @Override
    public Optional<Query<CoreMember<ObjectId>>> asQueryForUser(UUID userUUID) {
        return asQuery().map(q -> q.field("userUUID").equal(userUUID));
    }

    @Override
    public Optional<Query<CoreMember<ObjectId>>> asQueryForUser(String userName) {
        return asQuery().map(q -> q.field("userName").containsIgnoreCase(userName));
    }

    @Override
    public Optional<Query<CoreMember<ObjectId>>> asQueryForIpAddress(String ipAddress) {
        return asQuery().map(q -> q.field("ipAddress").equal(ipAddress));
    }
}
