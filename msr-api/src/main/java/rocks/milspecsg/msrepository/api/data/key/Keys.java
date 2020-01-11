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

package rocks.milspecsg.msrepository.api.data.key;

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

    public static final Key<Boolean> USE_SHARED_ENVIRONMENT = new Key<Boolean>("USE_SHARED_ENVIRONMENT", true) {
    };
    public static final Key<Boolean> USE_SHARED_CREDENTIALS = new Key<Boolean>("USE_SHARED_CREDENTIALS", true) {
    };
    public static final Key<String> DATA_STORE_NAME = new Key<String>("DATA_STORE_NAME", "xodus") {
    };
    public static final Key<String> MONGODB_HOSTNAME = new Key<String>("MONGODB_HOSTNAME", "localhost") {
    };
    public static final Key<Integer> MONGODB_PORT = new Key<Integer>("MONGODB_PORT", 27017) {
    };
    public static final Key<String> MONGODB_DBNAME = new Key<String>("MONGODB_DBNAME", "mscore") {
    };
    public static final Key<String> MONGODB_USERNAME = new Key<String>("MONGODB_USERNAME", "admin") {
    };
    public static final Key<String> MONGODB_PASSWORD = new Key<String>("MONGODB_PASSWORD", "password") {
    };
    public static final Key<String> MONGODB_AUTH_DB = new Key<String>("MONGODB_AUTH_DB", "admin") {
    };
    public static final Key<Boolean> MONGODB_USE_AUTH = new Key<Boolean>("MONGODB_USEAUTH", false) {
    };

    static {
        registerKey(USE_SHARED_ENVIRONMENT);
        registerKey(USE_SHARED_CREDENTIALS);
        registerKey(DATA_STORE_NAME);
        registerKey(MONGODB_HOSTNAME);
        registerKey(MONGODB_PORT);
        registerKey(MONGODB_DBNAME);
        registerKey(MONGODB_USERNAME);
        registerKey(MONGODB_PASSWORD);
        registerKey(MONGODB_AUTH_DB);
        registerKey(MONGODB_USE_AUTH);
    }
}
