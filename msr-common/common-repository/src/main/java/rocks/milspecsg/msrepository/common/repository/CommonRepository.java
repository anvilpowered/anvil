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

package rocks.milspecsg.msrepository.common.repository;

import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.api.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.api.model.ObjectWithId;
import rocks.milspecsg.msrepository.common.component.CommonComponent;
import rocks.milspecsg.msrepository.common.storageservice.CommonStorageService;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends CommonComponent<TKey, TDataStore>
    implements Repository<TKey, T, TDataStore>,
    CommonStorageService<TKey, T, TDataStore> {

    protected CommonRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<Integer>> getCreatedUtcTimeStampSeconds(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtcTimeStampSeconds));
    }

    @Override
    public CompletableFuture<Optional<Long>> getCreatedUtcTimeStampMillis(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtcTimeStampMillis));
    }

    @Override
    public CompletableFuture<Optional<Date>> getCreatedUtcDate(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtcDate));
    }
}
