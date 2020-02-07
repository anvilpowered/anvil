/*
 *   Anvil - MilSpecSG
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

package rocks.milspecsg.anvil.common.plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import rocks.milspecsg.anvil.api.Anvil;
import rocks.milspecsg.anvil.api.Environment;
import rocks.milspecsg.anvil.api.plugin.Plugin;

import java.util.function.Consumer;

/**
 * A simple default implementation of {@link Plugin} that only registers a
 * single {@link Environment} with the same name as this {@link Plugin}
 */
public abstract class CommonPlugin<TPluginContainer> implements Plugin<TPluginContainer> {

    @Inject
    protected TPluginContainer pluginContainer;

    protected Environment environment;

    protected CommonPlugin(String name, Consumer<Environment> whenReady, Injector injector, Module... modules) {
        Anvil.environmentBuilder()
            .setName(name)
            .setRootInjector(injector)
            .addModules(modules)
            .whenReady(e -> environment = e)
            .whenReady(whenReady)
            .register(this);
    }

    protected CommonPlugin(String name, Injector injector, Module... modules) {
        Anvil.environmentBuilder()
            .setName(name)
            .setRootInjector(injector)
            .addModules(modules)
            .whenReady(e -> environment = e)
            .register(this);
    }

    protected CommonPlugin() {
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return environment.getName();
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
