/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.api.repository;

import jetbrains.exodus.entitystore.*;
import rocks.milspecsg.msrepository.api.cache.CacheService;
import rocks.milspecsg.msrepository.model.data.dbo.Mappable;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface XodusRepository<
    T extends ObjectWithId<EntityId> & Mappable<Entity>,
    C extends CacheService<EntityId, T, PersistentEntityStore>>
    extends Repository<EntityId, T, C, PersistentEntityStore> {

    CompletableFuture<List<T>> getAll(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Optional<T>> getOne(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    CompletableFuture<Boolean> delete(Function<? super StoreTransaction, ? extends Iterable<Entity>> query);

    Function<? super StoreTransaction, ? extends Iterable<Entity>> asQuery(EntityId id);
}
