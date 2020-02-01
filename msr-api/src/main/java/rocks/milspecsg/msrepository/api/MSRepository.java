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

package rocks.milspecsg.msrepository.api;

import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import rocks.milspecsg.msrepository.api.data.Environment;
import rocks.milspecsg.msrepository.api.data.key.Key;
import rocks.milspecsg.msrepository.api.data.key.Keys;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.util.PluginInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "unused", "UnstableApiUsage"})
public final class MSRepository {

    private static final Map<TypeToken<?>, Supplier<?>> bindings = new HashMap<>();
    private static final Map<String, Environment> environments = new HashMap<>();

    private MSRepository() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static <T> Supplier<T> provideSupplier(TypeToken<T> typeToken) {
        try {
            return (Supplier<T>) Objects.requireNonNull(typeToken);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Could not find binding for " + typeToken.getRawType().getName(), e);
        }
    }

    public static <T> Supplier<T> provideSupplier(Class<T> clazz) {
        return provideSupplier(TypeToken.of(clazz));
    }

    public static <T> Supplier<T> provideSupplier(String name) {
        try {
            Supplier<T>[] suppliers = new Supplier[1];
            for (Map.Entry<TypeToken<?>, Supplier<?>> entry : bindings.entrySet()) {
                if (entry.getKey().getRawType().getName().equalsIgnoreCase(name)) {
                    suppliers[0] = (Supplier<T>) entry.getValue();
                    break;
                }
            }
            return Objects.requireNonNull(suppliers[0]);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Could not find binding for " + name, e);
        }
    }

    public static <T> T provide(TypeToken<T> typeToken) {
        return provideSupplier(typeToken).get();
    }

    public static <T> T provide(Class<T> clazz) {
        return provideSupplier(clazz).get();
    }

    public static <T> T provide(String name) {
        return MSRepository.<T>provideSupplier(name).get();
    }

    protected static <T> void registerBinding(TypeToken<T> typeToken, Supplier<T> supplier) {
        bindings.put(typeToken, supplier);
    }

    public static Environment getCoreEnvironment() {
        return Objects.requireNonNull(environments.get("mscore"), "Global environment not loaded");
    }

    public static Environment getEnvironmentUnsafe(String name) {
        return Objects.requireNonNull(environments.get(name), "Could not find environment with name " + name);
    }

    public static Optional<Environment> getEnvironment(String name) {
        return Optional.ofNullable(environments.get(name));
    }

    public static <TString> void registerEnvironment(
        String name,
        Injector injector,
        com.google.inject.Key<? extends PluginInfo<TString>> pluginInfoKey,
        com.google.inject.Key<? extends Registry> registryKey
    ) {
        if (environments.containsKey(name)) {
            throw new IllegalArgumentException("Environment with name " + name + " already exists");
        }
        environments.put(name, new Environment() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Injector getInjector() {
                return injector;
            }

            @Override
            public PluginInfo<TString> getPluginInfo() {
                return injector.getInstance(pluginInfoKey);
            }

            @Override
            public Registry getRegistry() {
                return injector.getInstance(registryKey);
            }
        });
    }

    public static <TString> void registerEnvironment(
        String name,
        Injector injector,
        com.google.inject.Key<? extends PluginInfo<TString>> pluginInfoKey
    ) {
        registerEnvironment(
            name,
            injector,
            pluginInfoKey,
            com.google.inject.Key.get(Registry.class)
        );
    }

    public static <T> T resolveForSharedEnvironment(Key<T> key, Registry registry) {
        Registry coreRegistry = getCoreEnvironment().getRegistry();
        if (registry.getOrDefault(Keys.USE_SHARED_ENVIRONMENT)) {
            if (key.equals(Keys.DATA_STORE_NAME)
                || key.equals(Keys.MONGODB_HOSTNAME)
                || key.equals(Keys.MONGODB_PORT)) {
                return coreRegistry.getOrDefault(key);
            } else if (registry.getOrDefault(Keys.USE_SHARED_CREDENTIALS)) {
                if (key.equals(Keys.MONGODB_USERNAME)
                    || key.equals(Keys.MONGODB_PASSWORD)
                    || key.equals(Keys.MONGODB_AUTH_DB)
                    || key.equals(Keys.MONGODB_USE_AUTH)) {
                    return coreRegistry.getOrDefault(key);
                }
            }
        }
        return registry.getOrDefault(key);
    }
}
