/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.common.plugin.AnvilCore;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.nukkit.listener.NukkitPlayerListener;
import org.anvilpowered.anvil.nukkit.module.ApiNukkitModule;
import org.anvilpowered.anvil.nukkit.module.NukkitModule;

import java.util.Set;

public class AnvilCoreNukkit extends PluginBase
    implements org.anvilpowered.anvil.api.plugin.Plugin<Plugin> {

    protected final Inner inner;

    public AnvilCoreNukkit() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Plugin.class).toInstance(AnvilCoreNukkit.this);
            }
        };
        Injector injector = Guice.createInjector(module);
        inner = injector.getInstance(Inner.class);
    }

    private static final class Inner extends AnvilCore<Plugin> {

        @Inject
        public Inner(Injector injector) {
            super(injector, new NukkitModule());
        }
    }

    @Override
    public void onEnable() {
        AnvilImpl.completeInitialization(new ApiNukkitModule());
        Server.getInstance().getPluginManager().registerEvents(
            inner.getPrimaryEnvironment()
                .getInjector().getInstance(NukkitPlayerListener.class),
            this
        );
    }

    @Override
    public String toString() {
        return AnvilCorePluginInfo.id;
    }

    @Override
    public int compareTo(org.anvilpowered.anvil.api.plugin.Plugin<Plugin> o) {
        return inner.compareTo(o);
    }

    @Override
    public AnvilCoreNukkit getPluginContainer() {
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
