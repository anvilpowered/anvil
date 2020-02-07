/*
 *   Anvil - MilSpecSG
 *   Copyright (C) 2020 Cableguy20
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

package rocks.milspecsg.anvil.common.repository;

import rocks.milspecsg.anvil.api.datastore.DataStoreContext;
import rocks.milspecsg.anvil.api.model.ObjectWithId;
import rocks.milspecsg.anvil.api.repository.Repository;
import rocks.milspecsg.anvil.common.component.CommonComponent;
import rocks.milspecsg.anvil.common.storageservice.CommonStorageService;

import java.time.Instant;
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
    public CompletableFuture<Optional<Instant>> getCreatedUtc(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtc));
    }
}
