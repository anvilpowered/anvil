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

package rocks.milspecsg.mscore.api.coremember.repository;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import rocks.milspecsg.mscore.api.model.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.repository.MongoRepository;

import java.util.UUID;

public interface MongoCoreMemberRepository
    extends CoreMemberRepository<ObjectId, Datastore>,
    MongoRepository<CoreMember<ObjectId>> {

    Query<CoreMember<ObjectId>> asQueryForUser(UUID userUUID);

    Query<CoreMember<ObjectId>> asQueryForUser(String userName);

    Query<CoreMember<ObjectId>> asQueryForIpAddress(String ipAddress);
}
