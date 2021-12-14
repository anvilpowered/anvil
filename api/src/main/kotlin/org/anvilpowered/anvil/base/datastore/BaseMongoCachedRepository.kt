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
package org.anvilpowered.anvil.base.datastore

import dev.morphia.Datastore
import java.util.Optional
import java.util.function.Function
import java.util.function.Supplier

@Deprecated("") // will probably break in 0.4
interface BaseMongoCachedRepository<T : ObjectWithId<ObjectId?>?, C : CacheService<ObjectId?, T, Datastore?>?> :
    BaseMongoRepository<T>, CachedRepository<ObjectId?, T, C, Datastore?> {
    override fun insertOne(item: T): CompletableFuture<Optional<T>?>? {
        return applyFromDBToCacheConditionally<T>(Supplier<Optional<T>> { super@BaseMongoRepository.insertOne(item).join() },
            BiConsumer<C, T> { obj: C, item: T -> obj.insertOne(item) })
    }

    override fun insert(list: List<T>?): CompletableFuture<List<T>?>? {
        return applyFromDBToCache<List<T>>(Supplier<List<T>> { super@BaseMongoRepository.insert(list).join() },
            BiConsumer<C, List<T>> { obj: C, list: List<T>? -> obj.insert(list) })
    }

    override fun getOne(id: ObjectId?): CompletableFuture<Optional<T>>? {
        return applyToBothConditionally<T>(Function<C, Optional<T>> { c: C -> c.getOne(id).join() },
            Supplier<Optional<T>> { super@BaseMongoRepository.getOne(id).join() })
    }
}
