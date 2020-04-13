/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.base.datastore;

import org.anvilpowered.anvil.api.datastore.DataStoreContext;
import org.anvilpowered.anvil.api.datastore.Repository;
import org.anvilpowered.anvil.api.model.ObjectWithId;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRepository<
    TKey,
    T extends ObjectWithId<TKey>,
    TDataStore>
    extends BaseComponent<TKey, TDataStore>
    implements Repository<TKey, T, TDataStore>,
    BaseStorageService<TKey, T, TDataStore> {

    protected BaseRepository(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        super(dataStoreContext);
    }

    @Override
    public CompletableFuture<Optional<Instant>> getCreatedUtc(TKey id) {
        return getOne(id).thenApplyAsync(o -> o.map(ObjectWithId::getCreatedUtc));
    }
}
