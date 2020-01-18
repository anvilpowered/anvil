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

package rocks.milspecsg.mscore.common.coremember.repository;

import com.google.inject.Inject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rocks.milspecsg.mscore.api.coremember.repository.MongoCoreMemberRepository;
import rocks.milspecsg.mscore.api.model.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.common.repository.CommonMongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommonMongoCoreMemberRepository
    extends CommonCoreMemberRepository<ObjectId, Datastore>
    implements CommonMongoRepository<CoreMember<ObjectId>>,
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
        return getOneForUser(userUUID).thenApplyAsync(optionalMember -> {
            if (optionalMember.isPresent()) {
                UpdateOperations<CoreMember<ObjectId>> updateOperations = createUpdateOperations();
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
                if (update(asQuery(optionalMember.get().getId()), updateOperations)) {
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
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQueryForUser(userUUID).get()));
    }

    @Override
    public CompletableFuture<Optional<CoreMember<ObjectId>>> getOneForUser(String userName) {
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(asQueryForUser(userName).get()));
    }

    @Override
    public CompletableFuture<List<CoreMember<ObjectId>>> getForIpAddress(String ipAddress) {
        return CompletableFuture.supplyAsync(() -> asQueryForIpAddress(ipAddress).asList());
    }

    @Override
    public Query<CoreMember<ObjectId>> asQueryForUser(UUID userUUID) {
        return asQuery().field("userUUID").equal(userUUID);
    }

    @Override
    public Query<CoreMember<ObjectId>> asQueryForUser(String userName) {
        return asQuery().field("userName").containsIgnoreCase(userName);
    }

    @Override
    public Query<CoreMember<ObjectId>> asQueryForIpAddress(String ipAddress) {
        return asQuery().field("ipAddress").equal(ipAddress);
    }
}
