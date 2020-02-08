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

package org.anvilpowered.anvil.common.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.Plugin;

import java.util.function.Consumer;

/**
 * A simple default implementation of {@link Plugin} that only registers a
 * single {@link Environment} with the same name as this {@link Plugin}
 */
public abstract class CommonPlugin<TPluginContainer> implements Plugin<TPluginContainer> {

    @Inject
    protected TPluginContainer pluginContainer;

    protected Environment environment;

    protected final String name;

    protected CommonPlugin(String name, Consumer<Environment> whenReady, Injector injector, Module... modules) {
        this.name = name;
        Anvil.environmentBuilder()
            .setName(name)
            .setRootInjector(injector)
            .addModules(modules)
            .whenReady(e -> environment = e)
            .whenReady(whenReady)
            .register(this);
    }

    protected CommonPlugin(String name, Injector injector, Module... modules) {
        this.name = name;
        Anvil.environmentBuilder()
            .setName(name)
            .setRootInjector(injector)
            .addModules(modules)
            .whenReady(e -> environment = e)
            .register(this);
    }

    protected CommonPlugin(String name) {
        this.name = name;
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
    public TPluginContainer getPluginContainer() {
        return pluginContainer;
    }

    @Override
    public Environment getPrimaryEnvironment() {
        return environment;
    }
}
