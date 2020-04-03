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

package org.anvilpowered.anvil.api.data.key;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Keys {

    private static Map<String, Key<?>> keyMap = new HashMap<>();

    private Keys() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static void registerKey(Key<?> key) {
        keyMap.put(key.getName(), key);
    }

    @SuppressWarnings("unchecked")
    public static <T> Key<T> resolveUnsafe(String name) {
        return (Key<T>) Objects.requireNonNull(keyMap.get(name));
    }

    public static <T> Optional<Key<T>> resolve(String name) {
        try {
            return Optional.ofNullable(resolveUnsafe(name));
        } catch (ClassCastException ignored) {
            return Optional.empty();
        }
    }

    public static final Key<String> SERVER_NAME = new Key<String>("SERVER_NAME", "server") {
    };
    public static final Key<Boolean> PROXY_MODE = new Key<Boolean>("PROXY_MODE", false) {
    };
    public static final Key<String> BASE_SCAN_PACKAGE = new Key<String>("BASE_SCAN_PACKAGE", "org.anvilpowered.anvil.common.model") {
    };
    public static final Key<Integer> CACHE_INVALIDATION_INTERVAL_SECONDS = new Key<Integer>("CACHE_INVALIDATION_INTERVAL_SECONDS", 30) {
    };
    public static final Key<Integer> CACHE_INVALIDATION_TIMOUT_SECONDS = new Key<Integer>("CACHE_INVALIDATION_TIMOUT_SECONDS", 300) {
    };
    public static final Key<Boolean> USE_SHARED_ENVIRONMENT = new Key<Boolean>("USE_SHARED_ENVIRONMENT", false) {
    };
    public static final Key<Boolean> USE_SHARED_CREDENTIALS = new Key<Boolean>("USE_SHARED_CREDENTIALS", false) {
    };
    public static final Key<String> DATA_DIRECTORY = new Key<String>("DATA_DIRECTORY", "anvil") {
    };
    public static final Key<String> DATA_STORE_NAME = new Key<String>("DATA_STORE_NAME", "xodus") {
    };
    public static final Key<String> MONGODB_CONNECTION_STRING = new Key<String>(
        "MONGODB_CONNECTION_STRING",
        "mongodb://admin:password@localhost:27017/anvil?authSource=admin") {
    };
    public static final Key<String> MONGODB_HOSTNAME = new Key<String>("MONGODB_HOSTNAME", "localhost") {
    };
    public static final Key<Integer> MONGODB_PORT = new Key<Integer>("MONGODB_PORT", 27017) {
    };
    public static final Key<String> MONGODB_DBNAME = new Key<String>("MONGODB_DBNAME", "anvil") {
    };
    public static final Key<String> MONGODB_USERNAME = new Key<String>("MONGODB_USERNAME", "admin") {
    };
    public static final Key<String> MONGODB_PASSWORD = new Key<String>("MONGODB_PASSWORD", "password") {
    };
    public static final Key<String> MONGODB_AUTH_DB = new Key<String>("MONGODB_AUTH_DB", "admin") {
    };
    public static final Key<Boolean> MONGODB_USE_AUTH = new Key<Boolean>("MONGODB_USE_AUTH", false) {
    };
    public static final Key<Boolean> MONGODB_USE_SRV = new Key<Boolean>("MONGODB_USE_SRV", false) {
    };
    public static final Key<Boolean> MONGODB_USE_CONNECTION_STRING = new Key<Boolean>("MONGODB_USE_CONNECTION_STRING", false) {
    };

    static {
        registerKey(SERVER_NAME);
        registerKey(PROXY_MODE);
        registerKey(BASE_SCAN_PACKAGE);
        registerKey(CACHE_INVALIDATION_INTERVAL_SECONDS);
        registerKey(CACHE_INVALIDATION_TIMOUT_SECONDS);
        registerKey(USE_SHARED_ENVIRONMENT);
        registerKey(USE_SHARED_CREDENTIALS);
        registerKey(DATA_DIRECTORY);
        registerKey(DATA_STORE_NAME);
        registerKey(MONGODB_HOSTNAME);
        registerKey(MONGODB_PORT);
        registerKey(MONGODB_DBNAME);
        registerKey(MONGODB_USERNAME);
        registerKey(MONGODB_PASSWORD);
        registerKey(MONGODB_AUTH_DB);
        registerKey(MONGODB_USE_AUTH);
        registerKey(MONGODB_USE_SRV);
    }
}
