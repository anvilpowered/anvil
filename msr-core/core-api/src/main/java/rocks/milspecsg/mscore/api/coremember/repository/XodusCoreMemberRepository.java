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

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface XodusCoreMemberRepository
    extends CoreMemberRepository<EntityId, PersistentEntityStore>,
    XodusRepository<CoreMember<EntityId>> {

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForUser(UUID userUUID);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForUser(String userName);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQueryForIpAddress(String ipAddress);

    CompletableFuture<Boolean> ban(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant endUtc, String reason);

    CompletableFuture<Boolean> unBan(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> mute(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, Instant endUtc, String reason);

    CompletableFuture<Boolean> unMute(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> setNickname(Function<? super StoreTransaction, ? extends Iterable<Entity>> query, String nickName);
}
