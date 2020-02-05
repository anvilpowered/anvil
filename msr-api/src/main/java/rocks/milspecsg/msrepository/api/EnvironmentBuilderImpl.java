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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class EnvironmentBuilderImpl implements Environment.Builder {

    private String name;
    private Injector rootInjector;
    private Plugin<?> plugin;
    private final Collection<Module> modules;
    private final Collection<Consumer<Environment>> listeners;
    private static final Collection<EnvironmentBuilderImpl> builders = new ArrayList<>();
    private static boolean alreadyCompleted = false;

    static void completeInitialization() {
        if (alreadyCompleted) {
            throw new IllegalStateException("This method should only be called exactly once (in MSCore)");
        }
        alreadyCompleted = true;
        Map<String, Collection<Consumer<Environment>>> listeners = new HashMap<>();
        Collection<EnvironmentImpl> environments = builders.stream().map(builder -> {
            final String name = builder.name;
            listeners.put(name, builder.listeners);
            return new EnvironmentImpl(builder.rootInjector, name, builder.plugin, builder.modules);
        }).collect(Collectors.toList());
        AbstractModule commonModule = new AbstractModule() {
            @Override
            protected void configure() {
                for (EnvironmentImpl environment : environments) {
                    bind(Environment.class)
                        .annotatedWith(Names.named(environment.getName()))
                        .toInstance(environment);
                }
                for (Plugin<?> plugin : MSRepository.plugins.values()) {
                    bind(new TypeLiteral<Plugin<?>>() {
                    }).annotatedWith(Names.named(plugin.getName()))
                        .toInstance(plugin);
                }
            }
        };
        for (EnvironmentImpl environment : environments) {
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
            MSRepository.registerEnvironment(environment, environment.getPlugin());
            injector.getInstance(Registry.class).load();
            listeners.get(environment.getName()).forEach(action -> action.accept(environment));
        }
    }

    EnvironmentBuilderImpl() {
        modules = new ArrayList<>();
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
