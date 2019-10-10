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

package rocks.milspecsg.msrepository.service.apirepository;

import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.repository.Repository;
import rocks.milspecsg.msrepository.datastore.DataStoreContext;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ApiRepository<TKey, T extends ObjectWithId<TKey>, C extends RepositoryCacheService<TKey, T>, TDataStore> implements Repository<TKey, T, C, TDataStore> {

    private DataStoreContext<TDataStore> dataStoreContext;

    public ApiRepository(DataStoreContext<TDataStore> dataStoreContext) {
        this.dataStoreContext = dataStoreContext;
    }

    @Override
    public DataStoreContext<TDataStore> getDataStoreContext() {
        return dataStoreContext;
    }

    @Override
    public <K> CompletableFuture<K> applyFromCache(Function<C, K> fromCache, Function<Optional<K>, K> fromDB) {
        return CompletableFuture.supplyAsync(() -> getRepositoryCacheService().map(fromCache)).thenApplyAsync(fromDB);
    }

}
