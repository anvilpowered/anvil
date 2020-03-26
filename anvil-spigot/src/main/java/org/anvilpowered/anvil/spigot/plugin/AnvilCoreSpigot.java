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

package org.anvilpowered.anvil.spigot.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.anvil.common.plugin.AnvilCore;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.spigot.listeners.SpigotPlayerListener;
import org.anvilpowered.anvil.spigot.module.ApiSpigotModule;
import org.anvilpowered.anvil.spigot.module.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class AnvilCoreSpigot extends JavaPlugin implements Plugin<JavaPlugin> {

    protected final Inner inner;

    public AnvilCoreSpigot() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(JavaPlugin.class).toInstance(AnvilCoreSpigot.this);
            }
        };
        Injector injector = Guice.createInjector(module);
        inner = injector.getInstance(Inner.class);
    }

    private static final class Inner extends AnvilCore<JavaPlugin> {
        @Inject
        public Inner(Injector injector) {
            super(injector, new SpigotModule());
        }
    }

    @Override
    public void onEnable() {
        AnvilImpl.completeInitialization(new ApiSpigotModule());
        Bukkit.getPluginManager().registerEvents(
            inner.getPrimaryEnvironment()
                .getInjector().getInstance(SpigotPlayerListener.class),
            this
        );
    }

    @Override
    public String toString() {
        return AnvilCorePluginInfo.id;
    }

    @Override
    public int compareTo(Plugin<JavaPlugin> o) {
        return inner.compareTo(o);
    }

    @Override
    public AnvilCoreSpigot getPluginContainer() {
        return this;
    }

    @Override
    public Environment getPrimaryEnvironment() {
        return inner.getPrimaryEnvironment();
    }

    @Override
    public Set<Environment> getAllEnvironments() {
        return inner.getAllEnvironments();
    }
}
