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

package rocks.milspecsg.msrepository.api.cache;

import rocks.milspecsg.msrepository.api.storageservice.DataStorageService;
import rocks.milspecsg.msrepository.datastore.DataStoreConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.List;
import java.util.function.Supplier;

public interface RepositoryCacheService<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore,
    TDataStoreConfig extends DataStoreConfig>
    extends CacheService<T>, DataStorageService<TKey, T, TDataStore, TDataStoreConfig> {

    /**
     * @param fromDB {@link Supplier<List>} that retrieves data from datastore
     * @return A list containing all elements that were successfully retrieved from the datastore and saved to the cache
     */
    Supplier<List<T>> save(Supplier<List<T>> fromDB);

}
