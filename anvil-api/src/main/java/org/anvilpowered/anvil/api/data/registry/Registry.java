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

package org.anvilpowered.anvil.api.data.registry;

import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.data.key.Key;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Registry {

    /**
     * Gets this registry's value for the provided {@link Key}
     * and throws an exception if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the value for
     * @return This registry's value for the provided {@link Key}
     * @throws NoSuchElementException If this registry has no value defined
     *                                for the provided {@link Key}
     */
    <T> T getUnsafe(Key<T> key);

    /**
     * Gets this registry's value for the provided {@link Key}
     * or {@link Optional#empty()} if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the value for
     * @return This registry's value for the provided {@link Key} or {@link Optional#empty()}
     */
    <T> Optional<T> get(Key<T> key);

    /**
     * Gets this registry's default value for the provided {@link Key}
     * or the fallback value if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the default value for
     * @return This registry's default value for the provided {@link Key} or the fallback value
     */
    default <T> T getDefault(Key<T> key) {
        return key.getFallbackValue();
    }

    /**
     * Gets this registry's value for the provided {@link Key}
     * or the default value if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the default value for
     * @return This registry's value for the provided {@link Key} or the default value
     */
    default <T> T getOrDefault(Key<T> key) {
        return get(key).orElse(getDefault(key));
    }

    /**
     * Sets this registry's value for the provided {@link Key}
     *
     * @param <T>   The value type of the provided {@link Key}
     * @param key   The {@link Key} to set the value for
     * @param value The value to set
     */
    <T> void set(Key<T> key, T value);

    /**
     * Removes this registry's value for the provided {@link Key}
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to set the value for
     */
    <T> void remove(Key<T> key);

    /**
     * Applies the provided transformation to this registry's
     * value for the provided {@link Key}
     *
     * @param <T>         The value type of the provided {@link Key}
     * @param key         The {@link Key} to transform the value for
     * @param transformer The transformation to apply
     */
    <T> void transform(Key<T> key, BiFunction<? super Key<T>, ? super T, ? extends T> transformer);

    /**
     * Applies the provided transformation to this registry's
     * value for the provided {@link Key}
     *
     * @param <T>         The value type of the provided {@link Key}
     * @param key         The {@link Key} to transform the value for
     * @param transformer The transformation to apply
     */
    <T> void transform(Key<T> key, Function<? super T, ? extends T> transformer);

    /**
     * Adds the provided value to this registry's
     * {@link Collection} value for the provided {@link Key}
     *
     * @param <T>   The value type of the provided {@link Key}
     * @param key   The {@link Key} of the collection
     *              to add the provided value to
     * @param value The value to add
     */
    <T> void addToCollection(Key<? extends Collection<T>> key, T value);

    /**
     * Removes the provided value from this registry's
     * {@link Collection} value for the provided {@link Key}
     *
     * @param <T>   The value type of the provided {@link Key}
     * @param key   The {@link Key} of the collection
     *              to add the provided value to
     * @param value The value to add
     */
    <T> void removeFromCollection(Key<? extends Collection<T>> key, T value);

    /**
     * Puts the provided key and value pair to this registry's
     * {@link Map} value for the provided {@link Key}
     *
     * @param <K>      The key type of the map value for the provided key
     * @param <T>      The value type of the map value for the provided key
     * @param key      The {@link Key} of the map to add the
     *                 provided key and value pair to
     * @param mapKey   The map key to add
     * @param mapValue The map value to add
     */
    <K, T> void putInMap(Key<? extends Map<K, T>> key, K mapKey, T mapValue);

    /**
     * Removes the provided key from this registry's
     * {@link Map} value for the provided {@link Key}
     *
     * @param <K>    The key type of the map value for the provided key
     * @param <T>    The value type of the map value for the provided key
     * @param key    The {@link Key} of the map to remove the provided mapKey from
     * @param mapKey The map key to remove
     */
    <K, T> void removeFromMap(Key<? extends Map<K, T>> key, K mapKey);

    /**
     * Runs all {@link Runnable listeners} that were
     * added before this call.
     *
     * @see Environment#reload()
     * @see #whenLoaded(Runnable)
     */
    void load();

    /**
     * Adds a {@link Runnable} to be loaded on {@link #load()}
     *
     * @param listener Listener to add
     * @see #load()
     */
    void whenLoaded(Runnable listener);
}
