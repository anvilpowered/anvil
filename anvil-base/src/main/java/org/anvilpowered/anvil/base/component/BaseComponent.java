/*
 *   Anvil - AnvilPowered
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

package org.anvilpowered.anvil.base.component;

import org.anvilpowered.anvil.api.component.Component;
import org.anvilpowered.anvil.api.datastore.DataStoreContext;

import java.util.Optional;

public abstract class BaseComponent<
    TKey,
    TDataStore>
    implements Component<TKey, TDataStore> {

    private DataStoreContext<TKey, TDataStore> dataStoreContext;

    protected BaseComponent(DataStoreContext<TKey, TDataStore> dataStoreContext) {
        this.dataStoreContext = dataStoreContext;
    }

    @Override
    public DataStoreContext<TKey, TDataStore> getDataStoreContext() {
        return dataStoreContext;
    }

    @Override
    public Class<TKey> getTKeyClass() {
        return dataStoreContext.getTKeyClass();
    }

    @Override
    public Optional<TKey> parse(Object object) {
        try {
            return Optional.of(parseUnsafe(object));
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            return Optional.empty();
        }
    }
}
