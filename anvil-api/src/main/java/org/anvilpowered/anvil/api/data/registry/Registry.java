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
    @RegistryScoped
    <T> T getUnsafe(Key<T> key);

    /**
     * Gets this registry's value for the provided {@link Key}
     * or {@link Optional#empty()} if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the value for
     * @return This registry's value for the provided {@link Key} or {@link Optional#empty()}
     */
    @RegistryScoped
    <T> Optional<T> get(Key<T> key);

    /**
     * Gets this registry's default value for the provided {@link Key}
     * or the fallback value if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the default value for
     * @return This registry's default value for the provided {@link Key} or the fallback value
     */
    @RegistryScoped
    default <T> T getDefault(Key<T> key) {
        return key.getFallbackValue();
    }

    /**
     * Gets this registry's value for the provided {@link Key}
     * or the default value if it is not present.
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the value or (if not present) the default value for
     * @return This registry's value for the provided {@link Key} or the default value
     */
    @RegistryScoped
    default <T> T getOrDefault(Key<T> key) {
        return get(key).orElse(getDefault(key));
    }

    /**
     * Similar to {@link #getOrDefault(Key)}, but performs additional (implementation specific)
     * checks that could potentially check other registries if certain requirements are met.
     * <strong>Use {@link #getOrDefault(Key)} unless you are sure you need this.</strong>
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to get the value for
     * @return The value for the provided {@link Key} as defined by the additional checks
     */
    @RegistryScoped
    default <T> T getExtraSafe(Key<T> key) {
        return getOrDefault(key);
    }

    /**
     * Replaces each "{}" in the provided template with the values of the provided keys.
     *
     * @return The formatted string
     * @throws IllegalArgumentException if the number of "{}" in the template != keys.length
     */
    default String format(String template, Key<?>... keys) {
        final int length = template.length();
        final int keyLength = keys.length;
        // false = not in bracket; expecting [^}]
        // true = read opening bracket; expecting [}]
        boolean inBracket = false;
        int keyIndex = 0;
        // assumes an average of 10 characters per placeholder (+2 because of braces)
        StringBuilder builder = new StringBuilder(length + keys.length * 8);
        for (int i = 0; i < length; i++) {
            final char candidate = template.charAt(i);
            if (!inBracket && candidate != '{') {
                // normal case
                builder.append(candidate);
            } else if (!inBracket) {
                // (candidate == '{') == true
                inBracket = true;
                if (++keyIndex >= keyLength) {
                    throw new IllegalArgumentException(
                        "Key index out of bounds. Not enough keys for number of placeholders"
                    );
                }
                builder.append(keys[keyIndex]);
            } else if (candidate == '}') {
                // inBracket == true
                inBracket = false;
            } else {
                throw new IllegalArgumentException("Expected '}' got '" + candidate
                    + "' at index " + i + " for template " + template);
            }
        }
        // make sure we have used each key
        if (++keyIndex != keyLength) {
            throw new IllegalArgumentException(
                "Key index out of bounds. Not enough placeholders for number of keys"
            );
        }
        return builder.toString();
    }

    /**
     * Sets this registry's value for the provided {@link Key}
     *
     * @param <T>   The value type of the provided {@link Key}
     * @param key   The {@link Key} to set the value for
     * @param value The value to set
     */
    @RegistryScoped
    <T> void set(Key<T> key, T value);

    /**
     * Removes this registry's value for the provided {@link Key}
     *
     * @param <T> The value type of the provided {@link Key}
     * @param key The {@link Key} to set the value for
     */
    @RegistryScoped
    <T> void remove(Key<T> key);

    /**
     * Applies the provided transformation to this registry's
     * value for the provided {@link Key}
     *
     * @param <T>         The value type of the provided {@link Key}
     * @param key         The {@link Key} to transform the value for
     * @param transformer The transformation to apply
     */
    @RegistryScoped
    <T> void transform(Key<T> key, BiFunction<? super Key<T>, ? super T, ? extends T> transformer);

    /**
     * Applies the provided transformation to this registry's
     * value for the provided {@link Key}
     *
     * @param <T>         The value type of the provided {@link Key}
     * @param key         The {@link Key} to transform the value for
     * @param transformer The transformation to apply
     */
    @RegistryScoped
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
    @RegistryScoped
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
    @RegistryScoped
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
    @RegistryScoped
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
    @RegistryScoped
    <K, T> void removeFromMap(Key<? extends Map<K, T>> key, K mapKey);

    /**
     * Runs all {@link Runnable listeners} that were
     * added before this call in the provided registryScope.
     *
     * @param registryScope The {@link RegistryScope} to load
     * @see Environment#reload()
     * @see #whenLoaded(Runnable)
     */
    @RegistryScoped
    void load(RegistryScope registryScope);

    /**
     * Runs all {@link Runnable listeners} that were
     * added before this call in the {@link RegistryScope#DEFAULT default scope}.
     *
     * @see Environment#reload()
     * @see #whenLoaded(Runnable)
     */
    @RegistryScoped
    default void load() {
        load(RegistryScope.DEFAULT);
    }

    /**
     * Adds a {@link Runnable} to be loaded on {@link #load()}.
     *
     * <p>
     * Listeners are grouped by order. Smaller orders run before larger ones.
     * The execution order within one order group is not guaranteed.
     * </p>
     * <p>
     * Please note that {@link ListenerRegistrationEnd#register()} must be invoked to
     * complete the registration.
     * </p>
     *
     * @param listener Listener to add
     * @return A {@link ListenerRegistrationEnd} for specifying additional parameters and
     * completing the registration.
     * @see #load()
     */
    ListenerRegistrationEnd whenLoaded(Runnable listener);

    interface ListenerRegistrationEnd {

        /**
         * Sets the order for the listener.
         * The default order is 0.
         *
         * @param order The order to run this listener in. Smaller is earlier.
         * @return {@code this}
         */
        ListenerRegistrationEnd order(int order);

        /**
         * Sets the scope for the listener.
         * The default scope is {@link RegistryScope#DEFAULT}.
         *
         * @param scope The scope to run this listener in.
         * @return {@code this}
         * @see RegistryScope
         * @see RegistryScoped
         */
        ListenerRegistrationEnd scope(RegistryScope scope);

        /**
         * Completes the listener registration.
         */
        void register();
    }
}
