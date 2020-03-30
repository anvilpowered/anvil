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

import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.Named;
import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;

import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public interface Environment extends Named, Comparable<Environment> {

    static <T> Binding<T> getBinding(String name, Injector injector) {
        long hash = ((long) name.hashCode()) * ((long) injector.hashCode());
        Binding<?>[] binding = {Anvil.bindingsCache.get(hash)};
        if (binding[0] != null) {
            return (Binding<T>) binding[0];
        }
        injector.getBindings().forEach((k, v) -> {
            if (k.getTypeLiteral().getType().getTypeName().contains(name)) {
                binding[0] = v;
            }
        });
        Binding<T> result = Objects.requireNonNull(
            (Binding<T>) binding[0],
            "Could not find binding for service: " + name + " in injector " + injector
        );
        Anvil.bindingsCache.put(hash, result);
        return result;
    }

    static <T> Key<T> getKey(String name, Injector injector) {
        return Environment.<T>getBinding(name, injector).getKey();
    }

    static <T> Provider<T> getProvider(String name, Injector injector) {
        return Environment.<T>getBinding(name, injector).getProvider();
    }

    static <T> T getInstance(String name, Injector injector) {
        return Environment.<T>getProvider(name, injector).get();
    }

    default <T> Binding<T> getBinding(String name) {
        return getBinding(name, getInjector());
    }

    default <T> Key<T> getKey(String name) {
        return getKey(name, getInjector());
    }

    default <T> Provider<T> getProvider(String name) {
        return getProvider(name, getInjector());
    }

    default <T> T getInstance(String name) {
        return getInstance(name, getInjector());
    }

    void reload();

    Injector getInjector();

    <TPluginContainer> Plugin<TPluginContainer> getPlugin();

    <TString> PluginInfo<TString> getPluginInfo();

    <TString, TCommandSource> TextService<TString, TCommandSource> getTextService();

    Registry getRegistry();

    interface Builder {

        Builder addModules(Module... modules);

        Builder addModules(Iterable<Module> modules);

        Builder addEarlyServices(Key<?>... keys);

        Builder addEarlyServices(Iterable<Key<?>> keys);

        Builder addEarlyServices(Class<?>... classes);

        Builder addEarlyServices(TypeLiteral<?>... typeLiterals);

        Builder addEarlyServices(TypeToken<?>... typeTokens);

        <T> Builder addEarlyServices(Key<T> key, Consumer<T> initializer);

        <T> Builder addEarlyServices(Class<T> clazz, Consumer<T> initializer);

        <T> Builder addEarlyServices(TypeLiteral<T> typeLiteral, Consumer<T> initializer);

        <T> Builder addEarlyServices(TypeToken<T> typeToken, Consumer<T> initializer);

        Builder setName(String name);

        Builder setRootInjector(Injector rootInjector);

        /**
         * This will load your root {@link CommandNode} as
         * defined by your guice module
         */
        Builder withRootCommand();

        /**
         * Called once, right after the environment is created.
         * Multiple can be added.
         */
        Builder whenReady(Consumer<Environment> listener);

        /**
         * Builds an {@link Environment} and registers it.
         *
         * @param plugin {@link Plugin} owner for this environment
         */
        void register(Plugin<?> plugin);
    }
}
