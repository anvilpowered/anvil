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

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.PersistentEntityStore;
import jetbrains.exodus.entitystore.StoreTransaction;
import rocks.milspecsg.mscore.api.model.coremember.CoreMember;
import rocks.milspecsg.msrepository.api.repository.XodusRepository;

import java.util.UUID;
import java.util.function.Function;

public interface XodusCoreMemberRepository
    extends CoreMemberRepository<EntityId, PersistentEntityStore>,
    XodusRepository<CoreMember<EntityId>> {

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForUser(UUID userUUID);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForUser(String userName);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForIpAddress(String ipAddress);
}
