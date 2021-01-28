/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.bungee;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.md_5.bungee.api.plugin.Plugin;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl;
import org.anvilpowered.anvil.bungee.listener.BungeePlayerListener;
import org.anvilpowered.anvil.bungee.module.ApiBungeeModule;
import org.anvilpowered.anvil.bungee.module.BungeeFallbackModule;
import org.anvilpowered.anvil.bungee.module.BungeeModule;

public class AnvilBungee extends Plugin {

    protected final Inner inner;

    public AnvilBungee() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Plugin.class).toInstance(AnvilBungee.this);
                bind(AnvilBungee.class).toInstance(AnvilBungee.this);
            }
        };
        Injector injector = Guice.createInjector(module);
        inner = new Inner(injector);
    }

    public final class Inner extends AnvilImpl {

        @Inject
        public Inner(Injector injector) {
            super(injector, new BungeeModule());
        }

        @Override
        protected void applyToBuilder(Environment.Builder builder) {
            super.applyToBuilder(builder);
            builder.addEarlyServices(BungeePlayerListener.class, t ->
                getProxy().getPluginManager().registerListener(AnvilBungee.this, t));
        }
    }

    @Override
    public void onEnable() {
        EnvironmentBuilderImpl.completeInitialization(new ApiBungeeModule(), new BungeeFallbackModule());
        getProxy().getPluginManager().registerListener(this,
            AnvilImpl.getEnvironment().getInjector().getInstance(BungeePlayerListener.class)
        );
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
