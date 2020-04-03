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
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.misc.BindingExtensions;
import org.anvilpowered.anvil.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class EnvironmentBuilderImpl implements Environment.Builder {

    private String name;
    private Injector rootInjector;
    private Plugin<?> plugin;
    private boolean withRootCommand = false;
    private final Collection<Module> modules;
    private final Map<Key<?>, Consumer<?>> earlyServices;
    private final Collection<Consumer<Environment>> listeners;
    private static final Collection<EnvironmentBuilderImpl> builders = new ArrayList<>();
    private static boolean alreadyCompleted = false;

    static void completeInitialization(Module platformModule) {
        if (alreadyCompleted) {
            throw new IllegalStateException("This method should only be called exactly once (in Anvil Common)");
        }
        alreadyCompleted = true;
        Map<String, Collection<Consumer<Environment>>> listeners = new HashMap<>();
        Collection<EnvironmentImpl> environments = builders.stream().map(builder -> {
            final String name = builder.name;
            listeners.put(name, builder.listeners);
            return new EnvironmentImpl(
                builder.rootInjector,
                name,
                builder.plugin,
                builder.withRootCommand,
                builder.modules,
                builder.earlyServices
            );
        }).collect(Collectors.toList());
        AbstractModule commonModule = new AbstractModule() {
            @Override
            protected void configure() {
                for (EnvironmentImpl environment : environments) {
                    bind(Environment.class)
                        .annotatedWith(Names.named(environment.getName()))
                        .toInstance(environment);
                }
                for (Plugin<?> plugin : ServiceManagerImpl.environmentManager.plugins.values()) {
                    bind(new TypeLiteral<Plugin<?>>() {
                    }).annotatedWith(Names.named(plugin.getName()))
                        .toInstance(plugin);
                }
            }
        };
        for (EnvironmentImpl environment : environments) {
            environment.addModule(platformModule);
            environment.addModule(commonModule);
            environment.addModule(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Environment.class).toInstance(environment);
                    bind(new TypeLiteral<Plugin<?>>() {
                    }).toInstance(environment.getPlugin());
                }
            });
            Injector injector = environment.getInjector();
            if (injector != null) {
                injector = injector.createChildInjector(environment.getModules());
            } else {
                injector = Guice.createInjector(environment.getModules());
            }
            environment.setInjector(injector);
            if ("anvil".equals(environment.getName())) {
                ((ServiceManagerImpl) Anvil.getServiceManager())
                    .setInjector(injector);
            }
            ServiceManagerImpl.environmentManager
                .registerEnvironment(environment, environment.getPlugin());
            for (Map.Entry<Key<?>, Consumer<?>> entry
                : environment.getEarlyServices().entrySet()) {
                ((Consumer) entry.getValue())
                    .accept(injector.getInstance(entry.getKey()));
            }
            if (environment.withRootCommand()) {
                environment.getInstance(CommandNode.class.getCanonicalName());
            }
            injector.getInstance(Registry.class).load();
            listeners.get(environment.getName()).forEach(action -> action.accept(environment));
        }
    }

    EnvironmentBuilderImpl() {
        modules = new ArrayList<>();
        earlyServices = new HashMap<>();
        listeners = new ArrayList<>();
    }

    @Override
    public Environment.Builder addModules(Module... modules) {
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    @Override
    public Environment.Builder addModules(Iterable<Module> modules) {
        modules.forEach(this.modules::add);
        return this;
    }

    private <T> Environment.Builder addEarlyServices(
        Stream<T> keys,
        Function<T, Key<?>> keyTransformer
    ) {
        keys.forEach(key -> earlyServices.put(keyTransformer.apply(key), t -> {
        }));
        return this;
    }

    @Override
    public Environment.Builder addEarlyServices(Key<?>... keys) {
        return addEarlyServices(Stream.of(keys), Function.identity());
    }

    @Override
    public Environment.Builder addEarlyServices(Iterable<Key<?>> keys) {
        return addEarlyServices(StreamSupport.stream(keys.spliterator(), false),
            Function.identity());
    }

    @Override
    public Environment.Builder addEarlyServices(Class<?>... classes) {
        return addEarlyServices(Stream.of(classes), Key::get);
    }

    @Override
    public Environment.Builder addEarlyServices(TypeLiteral<?>... typeLiterals) {
        return addEarlyServices(Stream.of(typeLiterals), Key::get);
    }

    @Override
    public Environment.Builder addEarlyServices(TypeToken<?>... typeTokens) {
        return addEarlyServices(Stream.of(typeTokens), BindingExtensions::getKey);
    }

    @Override
    public <T> Environment.Builder addEarlyServices(Key<T> key, Consumer<T> initializer) {
        earlyServices.put(key, initializer);
        return this;
    }

    @Override
    public <T> Environment.Builder addEarlyServices(Class<T> clazz, Consumer<T> initializer) {
        earlyServices.put(Key.get(clazz), initializer);
        return this;
    }

    @Override
    public <T> Environment.Builder addEarlyServices(TypeLiteral<T> typeLiteral, Consumer<T> initializer) {
        earlyServices.put(Key.get(typeLiteral), initializer);
        return this;
    }

    @Override
    public <T> Environment.Builder addEarlyServices(TypeToken<T> typeToken, Consumer<T> initializer) {
        earlyServices.put(BindingExtensions.getKey(typeToken), initializer);
        return this;
    }

    @Override
    public Environment.Builder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Environment.Builder setRootInjector(Injector rootInjector) {
        this.rootInjector = rootInjector;
        return this;
    }

    @Override
    public Environment.Builder withRootCommand() {
        withRootCommand = true;
        return this;
    }

    @Override
    public Environment.Builder whenReady(Consumer<Environment> listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public void register(Plugin<?> plugin) {
        Objects.requireNonNull(name, "Could not register environment: name is required!");
        Objects.requireNonNull(plugin, "Could not register environment: name is required!");
        this.plugin = plugin;
        builders.add(this);
    }
}
