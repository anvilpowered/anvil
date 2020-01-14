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

package rocks.milspecsg.msrepository.common.data.registry;

import com.google.inject.Singleton;
import rocks.milspecsg.msrepository.api.data.key.Key;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.data.registry.RegistryLoadedListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

@Singleton
@SuppressWarnings("unchecked")
public class CommonRegistry implements Registry {

    protected final Map<Key<?>, Object> defaultMap, valueMap;
    protected final Collection<RegistryLoadedListener> registryLoadedListeners;

    public CommonRegistry() {
        defaultMap = new HashMap<>();
        valueMap = new HashMap<>();
        registryLoadedListeners = new LinkedList<>();
    }

    @Override
    public <T> Optional<T> get(Key<T> key) {
        try {
            return Optional.ofNullable((T) valueMap.get(key));
        } catch (ClassCastException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T> T getDefault(Key<T> key) {
        T result = null;
        try {
            result = (T) defaultMap.get(key);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return result == null ? key.getFallbackValue() : result;
    }

    @Override
    public void load(Object plugin) {
        registryLoadedListeners.forEach(listener -> listener.loaded(plugin));
    }

    @Override
    public void addRegistryLoadedListener(RegistryLoadedListener registryLoadedListener) {
        registryLoadedListeners.add(registryLoadedListener);
    }
}
