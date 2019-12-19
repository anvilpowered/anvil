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

package rocks.milspecsg.msrepository.service.common.storageservice;

import rocks.milspecsg.msrepository.api.storageservice.StorageService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CommonStorageService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends StorageService<TKey, T, TDataStore, TDataStoreConfig> {

    @Override
    default T generateEmpty() {
        Class<T> tClass = getTClass();
        try {
            return tClass.getConstructor().newInstance();
        } catch (Exception e) {
            String message = "There was an error creating an instance of " + tClass.getName() + "! Make sure it has an accessible no-args constructor!";
            System.err.println(message);
            throw new IllegalStateException(message, e);
        }
    }

    @Override
    default CompletableFuture<Optional<T>> parseAndGetOne(Object id) {
        return parse(id).map(this::getOne).orElse(CompletableFuture.completedFuture(Optional.empty()));
    }

    @Override
    default CompletableFuture<Boolean> parseAndDeleteOne(Object id) {
        return parse(id).map(this::deleteOne).orElse(CompletableFuture.completedFuture(false));
    }
}
