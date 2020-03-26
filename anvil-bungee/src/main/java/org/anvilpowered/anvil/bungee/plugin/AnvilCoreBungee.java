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

package org.anvilpowered.anvil.bungee.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.bungee.listeners.BungeePlayerListener;
import org.anvilpowered.anvil.bungee.module.ApiBungeeModule;
import org.anvilpowered.anvil.bungee.module.BungeeModule;
import org.anvilpowered.anvil.common.plugin.AnvilCore;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;

import java.util.Set;

public class AnvilCoreBungee extends Plugin
    implements org.anvilpowered.anvil.api.plugin.Plugin<Plugin> {

    protected final Inner inner;

    public AnvilCoreBungee() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Plugin.class).toInstance(AnvilCoreBungee.this);
            }
        };
        Injector injector = Guice.createInjector(module);
        inner = injector.getInstance(Inner.class);
    }

    private static final class Inner extends AnvilCore<Plugin> {
        @Inject
        public Inner(Injector injector) {
            super(injector, new BungeeModule());
        }
    }

    @Override
    public void onEnable() {
        AnvilImpl.completeInitialization(new ApiBungeeModule());
        getProxy().getPluginManager().registerListener(this,
            inner.getPrimaryEnvironment()
                .getInjector().getInstance(BungeePlayerListener.class)
        );
    }

    @Override
    public String toString() {
        return AnvilCorePluginInfo.id;
    }

    @Override
    public String getName() {
        return AnvilCorePluginInfo.name;
    }

    @Override
    public int compareTo(org.anvilpowered.anvil.api.plugin.Plugin<Plugin> o) {
        return inner.compareTo(o);
    }

    @Override
    public boolean equals(Object obj) {
        return inner.equals(obj);
    }

    @Override
    public AnvilCoreBungee getPluginContainer() {
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
