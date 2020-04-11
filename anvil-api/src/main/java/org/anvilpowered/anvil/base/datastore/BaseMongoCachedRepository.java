/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.base.datastore;

import com.mongodb.WriteResult;
import org.anvilpowered.anvil.api.datastore.CacheService;
import org.anvilpowered.anvil.api.datastore.CachedRepository;
import org.anvilpowered.anvil.api.datastore.StorageService;
import org.anvilpowered.anvil.api.model.ObjectWithId;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface BaseMongoCachedRepository<
    T extends ObjectWithId<ObjectId>,
    C extends CacheService<ObjectId, T, Datastore>>
    extends BaseMongoRepository<T>, CachedRepository<ObjectId, T, C, Datastore> {

    @Override
    default CompletableFuture<Optional<T>> insertOne(T item) {
        return applyFromDBToCacheConditionally(() -> BaseMongoRepository.super.insertOne(item).join(), StorageService::insertOne);
    }

    @Override
    default CompletableFuture<List<T>> insert(List<T> list) {
        return applyFromDBToCache(() -> BaseMongoRepository.super.insert(list).join(), StorageService::insert);
    }

    @Override
    default CompletableFuture<Optional<T>> getOne(ObjectId id) {
        return applyToBothConditionally(c -> c.getOne(id).join(), () -> BaseMongoRepository.super.getOne(id).join());
    }

    @Override
    default CompletableFuture<WriteResult> delete(Query<T> query) {
        return applyFromDBToCache(() -> BaseMongoRepository.super.delete(query).join(), (c, w) -> {
            try {
                if (w.wasAcknowledged() && w.getN() > 0) {
                    c.deleteOne((ObjectId) w.getUpsertedId());
                }
            } catch (RuntimeException ignored) {
            }
        });
    }
}
