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

package rocks.milspecsg.msrepository.service.common.cache;

import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonRepositoryCacheService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CommonCacheService<T>
    implements RepositoryCacheService<TKey, T, TDataStore, TDataStoreConfig> {

    protected CommonRepositoryCacheService(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public CompletableFuture<Optional<T>> getOne(TKey id) {
        return CompletableFuture.supplyAsync(() -> getOne(dbo -> dbo.getId().equals(id)));
    }

    @Override
    public CompletableFuture<Boolean> deleteOne(TKey id) {
        return CompletableFuture.supplyAsync(() -> deleteOne(dbo -> dbo.getId().equals(id)).isPresent());
    }

}
