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

package org.anvilpowered.anvil.api.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class Keys {

    private static final String GLOBAL_NAMESPACE
        = "global";

    private static final Table<String, String, Key<?>> keys
        = HashBasedTable.create();

    private static final Map<String, Map<String, Key<?>>> localAndGlobalCache
        = new HashMap<>();

    private Keys() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    /**
     * <p>
     * Used to start the registration of keys in a namespace.
     * </p>
     * <br>
     * <p>
     * Example usage:
     * </p>
     * <pre><code>
     * static {
     *     Keys.startRegistration("ontime")
     *         .register(RANKS)
     *         .register(CHECK_PERMISSION)
     *         .register(CHECK_EXTENDED_PERMISSION)
     *         .register(EDIT_PERMISSION)
     *         .register(IMPORT_PERMISSION);
     * }
     * </code></pre>
     *
     * @param nameSpace The namespace to register the keys in. Usually the name of the plugin.
     * @return A {@link KeyRegistrationEnd} instance for registering keys
     */
    public static KeyRegistrationEnd startRegistration(String nameSpace) {
        return new KeyRegistrationEnd(nameSpace);
    }

    public final static class KeyRegistrationEnd {

        private final String nameSpace;

        KeyRegistrationEnd(String nameSpace) {
            this.nameSpace = nameSpace;
        }

        private void checkName(String nameSpace, String name) {
            if (keys.contains(nameSpace, name)) {
                throw new IllegalArgumentException("The provided key " + name + " conflicts with a"
                    + " key of the same name in the " + nameSpace + " namespace.");
            }
        }

        /**
         * Registers the provided key.
         *
         * @param key The {@link Key} to register
         * @return {@code this}
         */
        public KeyRegistrationEnd register(Key<?> key) {
            final String name = key.getName();
            checkName(nameSpace, name);
            keys.put(nameSpace, key.getName(), key);
            return this;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> resolveUnsafe(String name, String nameSpace) {
        return (Key<T>) Preconditions.checkNotNull(keys.get(nameSpace, name));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Key<T>> resolve(String name, String nameSpace) {
        return Optional.ofNullable((Key<T>) keys.get(nameSpace, name));
    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> resolveUnsafe(String name) {
        return (Key<T>) resolve(name).orElseThrow(() ->
            new IllegalArgumentException("Could not resolve key " + name));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Key<T>> resolve(String name) {
        @Nullable
        Key<T> candidate = (Key<T>) keys.get(GLOBAL_NAMESPACE, name);
        if (candidate != null) {
            return Optional.of(candidate);
        }
        Iterator<Key<?>> it = keys.column(name).values().iterator();
        if (it.hasNext()) {
            return Optional.of((Key<T>) it.next());
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<Key<T>> resolveLocalAndGlobal(String name, String nameSpace) {
        @Nullable
        Key<T> candidate = (Key<T>) keys.get(nameSpace, name);
        if (candidate != null) {
            return Optional.of(candidate);
        }
        return Optional.ofNullable((Key<T>) keys.get(GLOBAL_NAMESPACE, name));
    }

    public static Map<String, Key<?>> getAll(String nameSpace) {
        Map<String, Key<?>> result = localAndGlobalCache.get(nameSpace);
        if (result != null) {
            return result;
        }
        result = ImmutableMap.<String, Key<?>>builder()
            .putAll(keys.row(nameSpace))
            .putAll(keys.row(GLOBAL_NAMESPACE))
            .build();
        localAndGlobalCache.put(nameSpace, result);
        return result;
    }

    public static final Key<String> SERVER_NAME =
        Key.builder("SERVER_NAME", "server")
            .build();
    public static final Key<Boolean> PROXY_MODE =
        Key.builder("PROXY_MODE", false)
            .build();
    public static final Key<Boolean> REGEDIT_ALLOW_SENSITIVE =
        Key.builder("REGEDIT_ALLOW_SENSITIVE", false)
            .userImmutable()
            .build();
    public static final Key<String> BASE_SCAN_PACKAGE =
        Key.builder("BASE_SCAN_PACKAGE", "org.anvilpowered.anvil.common.model")
            .userImmutable()
            .build();
    public static final Key<Integer> CACHE_INVALIDATION_INTERVAL_SECONDS =
        Key.builder("CACHE_INVALIDATION_INTERVAL_SECONDS", 30)
            .build();
    public static final Key<Integer> CACHE_INVALIDATION_TIMOUT_SECONDS =
        Key.builder("CACHE_INVALIDATION_TIMOUT_SECONDS", 300)
            .build();
    public static final Key<Boolean> USE_SHARED_ENVIRONMENT =
        Key.builder("USE_SHARED_ENVIRONMENT", false)
            .build();
    public static final Key<Boolean> USE_SHARED_CREDENTIALS =
        Key.builder("USE_SHARED_CREDENTIALS", false)
            .build();
    public static final Key<String> DATA_DIRECTORY =
        Key.builder("DATA_DIRECTORY", "anvil")
            .build();
    public static final Key<String> DATA_STORE_NAME =
        Key.builder("DATA_STORE_NAME", "xodus")
            .build();
    public static final Key<String> MONGODB_CONNECTION_STRING =
        Key.builder("MONGODB_CONNECTION_STRING", "mongodb://admin:password@localhost:27017/anvil?authSource=admin")
            .sensitive()
            .build();
    public static final Key<String> MONGODB_HOSTNAME =
        Key.builder("MONGODB_HOSTNAME", "localhost")
            .sensitive()
            .build();
    public static final Key<Integer> MONGODB_PORT =
        Key.builder("MONGODB_PORT", 27017)
            .sensitive()
            .build();
    public static final Key<String> MONGODB_DBNAME =
        Key.builder("MONGODB_DBNAME", "anvil")
            .sensitive()
            .build();
    public static final Key<String> MONGODB_USERNAME =
        Key.builder("MONGODB_USERNAME", "admin")
            .sensitive()
            .build();
    public static final Key<String> MONGODB_PASSWORD =
        Key.builder("MONGODB_PASSWORD", "password")
            .sensitive()
            .build();
    public static final Key<String> MONGODB_AUTH_DB =
        Key.builder("MONGODB_AUTH_DB", "admin")
            .sensitive()
            .build();
    public static final Key<Boolean> MONGODB_USE_AUTH =
        Key.builder("MONGODB_USE_AUTH", false)
            .sensitive()
            .build();
    public static final Key<Boolean> MONGODB_USE_SRV =
        Key.builder("MONGODB_USE_SRV", false)
            .sensitive()
            .build();
    public static final Key<Boolean> MONGODB_USE_CONNECTION_STRING =
        Key.builder("MONGODB_USE_CONNECTION_STRING", false)
            .sensitive()
            .build();
    public static final Key<String> REDIS_HOSTNAME =
        Key.builder("REDIS_HOSTNAME", "localhost")
            .sensitive()
            .build();
    public static final Key<Integer> REDIS_PORT =
        Key.builder("REDIS_PORT", 6379)
            .sensitive()
            .build();
    public static final Key<String> REDIS_PASSWORD =
        Key.builder("REDIS_PASSWORD", "username")
            .sensitive()
            .build();
    public static final Key<Boolean> REDIS_USE_AUTH =
        Key.builder("REDIS_USE_AUTH", false)
            .sensitive()
            .build();

    public static final Key<String> PLUGINS_PERMISSION =
        Key.builder("PLUGINS_PERMISSION", "anvil.admin.plugins")
            .build();
    public static final Key<String> REGEDIT_PERMISSION =
        Key.builder("REGEDIT_PERMISSION", "anvil.admin.regedit")
            .build();
    public static final Key<String> RELOAD_PERMISSION =
        Key.builder("RELOAD_PERMISSION", "anvil.admin.reload")
            .build();

    static {
        startRegistration(GLOBAL_NAMESPACE)
            .register(SERVER_NAME)
            .register(PROXY_MODE)
            .register(REGEDIT_ALLOW_SENSITIVE)
            .register(BASE_SCAN_PACKAGE)
            .register(CACHE_INVALIDATION_INTERVAL_SECONDS)
            .register(CACHE_INVALIDATION_TIMOUT_SECONDS)
            .register(USE_SHARED_ENVIRONMENT)
            .register(USE_SHARED_CREDENTIALS)
            .register(DATA_DIRECTORY)
            .register(DATA_STORE_NAME)
            .register(MONGODB_HOSTNAME)
            .register(MONGODB_PORT)
            .register(MONGODB_DBNAME)
            .register(MONGODB_USERNAME)
            .register(MONGODB_PASSWORD)
            .register(MONGODB_AUTH_DB)
            .register(MONGODB_USE_AUTH)
            .register(MONGODB_USE_SRV)
            .register(REDIS_HOSTNAME)
            .register(REDIS_PORT)
            .register(REDIS_PASSWORD)
            .register(REDIS_USE_AUTH);

        startRegistration("anvil")
            .register(PLUGINS_PERMISSION)
            .register(REGEDIT_PERMISSION)
            .register(RELOAD_PERMISSION)
        ;
    }
}
