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

package org.anvilpowered.anvil.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.data.key.Key;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "unused", "UnstableApiUsage"})
public final class Anvil {

    private static final Map<TypeToken<?>, Supplier<?>> bindings = new HashMap<>();
    private static final Map<String, Environment> environments = new HashMap<>();
    static final Map<String, Plugin<?>> plugins = new HashMap<>();
    static final Map<Plugin<?>, Set<Environment>> pluginEnvironmentMap = new HashMap<>();
    static final Map<Long, Binding<?>> bindingsCache = new HashMap<>();

    private Anvil() {
        throw new AssertionError("**boss music** No instance for you!");
    }

    public static <T> Supplier<T> provideSupplier(TypeToken<T> typeToken) {
        return (Supplier<T>) Objects.requireNonNull(
            bindings.get(typeToken),
            "Could not find binding for " + typeToken.getRawType().getName()
        );
    }

    public static <T> Supplier<T> provideSupplier(Class<T> clazz) {
        return provideSupplier(TypeToken.of(clazz));
    }

    public static <T> Supplier<T> provideSupplier(String name) {
        Supplier<T>[] suppliers = new Supplier[1];
        for (Map.Entry<TypeToken<?>, Supplier<?>> entry : bindings.entrySet()) {
            if (entry.getKey().getRawType().getName().equalsIgnoreCase(name)) {
                suppliers[0] = (Supplier<T>) entry.getValue();
                break;
            }
        }
        return Objects.requireNonNull(suppliers[0], "Could not find binding for " + name);
    }

    public static <T> T provide(TypeToken<T> typeToken) {
        return provideSupplier(typeToken).get();
    }

    public static <T> T provide(Class<T> clazz) {
        return provideSupplier(clazz).get();
    }

    public static <T> T provide(String name) {
        return Anvil.<T>provideSupplier(name).get();
    }

    public static <T> void registerBinding(TypeToken<T> typeToken, Supplier<T> supplier) {
        bindings.put(typeToken, supplier);
    }

    public static Environment getCoreEnvironment() {
        return Objects.requireNonNull(environments.get("anvil"), "Global environment not loaded");
    }

    public static Environment getEnvironmentUnsafe(String name) {
        return Objects.requireNonNull(environments.get(name), "Could not find environment with name " + name);
    }

    public static Map<String, Environment> getEnvironments() {
        return ImmutableMap.copyOf(environments);
    }

    public static Stream<Environment> getEnvironmentsAsStream(Pattern pattern) {
        return environments.entrySet().stream().filter(e ->
            pattern.matcher(e.getKey()).matches()
        ).map(Map.Entry::getValue);
    }

    public static List<Environment> getEnvironments(Pattern pattern) {
        return getEnvironmentsAsStream(pattern).collect(Collectors.toList());
    }

    public static Optional<Environment> getEnvironment(Pattern pattern) {
        return getEnvironmentsAsStream(pattern).findAny();
    }

    public static Optional<Environment> getEnvironment(String name) {
        return Optional.ofNullable(environments.get(name));
    }

    public static Environment getEnvironment(Plugin<?> plugin) {
        return Objects.requireNonNull(environments.get(plugin.getName()));
    }

    public static Set<Environment> getEnvironments(Plugin<?> plugin) {
        return Objects.requireNonNull(pluginEnvironmentMap.get(plugin));
    }

    public static Environment.Builder environmentBuilder() {
        return new EnvironmentBuilderImpl();
    }

    /**
     * To be called by Anvil Core only
     */
    public static void completeInitialization(Module platformModule) {
        EnvironmentBuilderImpl.completeInitialization(platformModule);
    }

    static void registerEnvironment(Environment environment, Plugin<?> plugin) {
        final String name = environment.getName();
        if (environments.containsKey(name)) {
            throw new IllegalArgumentException("Environment with name " + name + " already exists");
        }
        environments.put(name, environment);
        Set<Environment> envs = pluginEnvironmentMap.get(plugin);
        if (envs == null) {
            envs = new TreeSet<>();
            envs.add(environment);
            pluginEnvironmentMap.put(plugin, envs);
            plugins.put(plugin.getName(), plugin);
        } else {
            envs.add(environment);
        }
    }

    public static <T> T resolveForSharedEnvironment(Key<T> key, Registry registry) {
        Registry coreRegistry = getCoreEnvironment().getRegistry();
        if (registry.getOrDefault(Keys.USE_SHARED_ENVIRONMENT)) {
            if (key.equals(Keys.DATA_STORE_NAME)
                || key.equals(Keys.MONGODB_HOSTNAME)
                || key.equals(Keys.MONGODB_PORT)
                || key.equals(Keys.MONGODB_USE_SRV)) {
                return coreRegistry.getOrDefault(key);
            } else if (registry.getOrDefault(Keys.USE_SHARED_CREDENTIALS)) {
                if (key.equals(Keys.MONGODB_USE_CONNECTION_STRING)
                    || key.equals(Keys.MONGODB_CONNECTION_STRING)
                    || key.equals(Keys.MONGODB_USERNAME)
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
