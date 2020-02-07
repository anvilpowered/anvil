/*
 *   Anvil - MilSpecSG
 *     Copyright (C) 2020 Cableguy20
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

package rocks.milspecsg.anvil.api.data.registry;

import rocks.milspecsg.anvil.api.data.key.Key;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Registry {

    <T> Optional<T> get(Key<T> key);

    default <T> T getDefault(Key<T> key) {
        return key.getFallbackValue();
    }

    default <T> T getOrDefault(Key<T> key) {
        return get(key).orElse(getDefault(key));
    }

    <T> void set(Key<T> key, T value);

    <T> void remove(Key<T> key);

    <T> void transform(Key<T> key, BiFunction<? super Key<T>, ? super T, ? extends T> transformer);

    <T> void transform(Key<T> key, Function<? super T, ? extends T> transformer);

    <T> void addToCollection(Key<? extends Collection<T>> key, T value);

    <T> void removeFromCollection(Key<? extends Collection<T>> key, T value);

    <K, T> void putInMap(Key<? extends Map<K, T>> key, K mapKey, T value);

    <K, T> void removeFromMap(Key<? extends Map<K, T>> key, K mapKey);

    void load();

    void addRegistryLoadedListener(RegistryLoadedListener registryLoadedListener);
}
