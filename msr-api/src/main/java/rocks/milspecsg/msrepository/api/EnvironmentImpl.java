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

import com.google.inject.Injector;
import com.google.inject.Module;
import rocks.milspecsg.msrepository.api.data.registry.Registry;
import rocks.milspecsg.msrepository.api.plugin.Plugin;
import rocks.milspecsg.msrepository.api.plugin.PluginInfo;

import java.util.Collection;

class EnvironmentImpl implements Environment {

    private Injector injector;
    private final String name;
    private final Plugin<?> plugin;
    private final Collection<Module> modules;

    EnvironmentImpl(Injector injector, String name, Plugin<?> plugin, Collection<Module> modules) {
        this.injector = injector;
        this.name = name;
        this.plugin = plugin;
        this.modules = modules;
    }

    void setInjector(Injector injector) {
        this.injector = injector;
    }

    void addModule(Module module) {
        this.modules.add(module);
    }

    Collection<Module> getModules() {
        return modules;
    }

    @Override
    public void reload() {
        getRegistry().load();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TPluginContainer> Plugin<TPluginContainer> getPlugin() {
        return (Plugin<TPluginContainer>) plugin;
    }

    @Override
    public <TString> PluginInfo<TString> getPluginInfo() {
        return getInstance(PluginInfo.class.getCanonicalName());
    }

    @Override
    public Registry getRegistry() {
        return getInjector().getInstance(Registry.class);
    }

    @Override
    public int compareTo(Environment o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Environment && getName().equals(((Environment) obj).getName());
    }
}
