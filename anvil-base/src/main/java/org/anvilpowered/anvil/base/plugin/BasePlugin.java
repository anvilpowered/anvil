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

package org.anvilpowered.anvil.base.plugin;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.Plugin;

import java.util.Collections;
import java.util.Set;

/**
 * A simple default implementation of {@link Plugin} that only registers a
 * single {@link Environment} with the same name as this {@link Plugin}
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class BasePlugin<TPluginContainer> implements Plugin<TPluginContainer> {

    @Inject
    protected TPluginContainer pluginContainer;

    protected Environment environment;

    protected final String name;

    protected BasePlugin(
        String name,
        Injector rootInjector,
        Module module,
        Key<?>... earlyServices
    ) {
        this.name = name;
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        Injector rootInjector,
        Module module,
        Class<?>... earlyServices
    ) {
        this.name = name;
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        Injector rootInjector,
        Module module,
        TypeLiteral<?>... earlyServices
    ) {
        this.name = name;
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        Injector rootInjector,
        Module module,
        TypeToken<?>... earlyServices
    ) {
        this.name = name;
        createDefaultBuilder(name, rootInjector, module)
            .addEarlyServices(earlyServices)
            .register(this);
    }

    protected BasePlugin(
        String name,
        Injector rootInjector,
        Module module
    ) {
        this.name = name;
        createDefaultBuilder(name, rootInjector, module)
            .register(this);
    }

    protected BasePlugin(String name) {
        this.name = name;
    }

    protected Environment.Builder createDefaultBuilder(
        String name,
        Injector rootInjector,
        Module module
    ) {
        Environment.Builder builder = Anvil.getEnvironmentBuilder()
            .setName(name)
            .setRootInjector(rootInjector)
            .addModules(module)
            .whenReady(e -> environment = e)
            .whenReady(this::whenReady);
        applyToBuilder(builder);
        return builder;
    }

    protected void applyToBuilder(Environment.Builder builder) {
    }

    protected void whenReady(Environment environment) {
        environment.getStringResult().builder()
            .append(environment.getPluginInfo().getPrefix())
            .aqua().append("Loaded!")
            .sendToConsole();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Plugin<TPluginContainer> o) {
        return name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Plugin && name.equals(((Plugin<?>) obj).getName());
    }

    @Override
    public TPluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Override
    public Environment getPrimaryEnvironment() {
        return environment;
    }

    @Override
    public Set<Environment> getAllEnvironments() {
        return Collections.singleton(environment);
    }
}
