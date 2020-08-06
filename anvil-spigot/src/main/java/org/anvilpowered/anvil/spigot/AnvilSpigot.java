/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.spigot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.EnvironmentBuilderImpl;
import org.anvilpowered.anvil.api.registry.ConfigurationService;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.spigot.listener.SpigotPlayerListener;
import org.anvilpowered.anvil.spigot.module.ApiSpigotModule;
import org.anvilpowered.anvil.spigot.module.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class AnvilSpigot extends JavaPlugin {

    protected final Inner inner;

    public AnvilSpigot() {
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(JavaPlugin.class).toInstance(AnvilSpigot.this);
                bind(AnvilSpigot.class).toInstance(AnvilSpigot.this);
            }
        };
        Injector injector = Guice.createInjector(module);
        inner = new Inner(injector);
    }

    private final class Inner extends AnvilImpl {

        public Inner(Injector injector) {
            super(injector, new SpigotModule());
        }

        @Override
        protected void applyToBuilder(Environment.Builder builder) {
            super.applyToBuilder(builder);
            builder.addEarlyServices(SpigotPlayerListener.class, t ->
                Bukkit.getPluginManager().registerEvents(t, AnvilSpigot.this));
        }

        @Override
        protected void whenLoaded(Environment environment) {
            super.whenLoaded(environment);

            // Attempt to autodetect proxy mode
            boolean serverProxyMode = false;

            // Check for Paper-specific Velocity mode first
            try {
                Method getPaperConfigMethod = Server.Spigot.class.getMethod("getPaperConfig");
                YamlConfiguration paperConfig = (YamlConfiguration) getPaperConfigMethod
                    .invoke(Bukkit.spigot());
                serverProxyMode = paperConfig.getBoolean("settings.velocity-support.enabled", false);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Unable to get Paper configuration", e);
            } catch (NoSuchMethodException ignored) {
            }

            // Check BungeeCord mode if Velocity setting was not enabled or server implementation is not Paper
            if (!serverProxyMode) {
                serverProxyMode = Bukkit.spigot().getConfig()
                    .getBoolean("settings.bungeecord", false);
            }

            ConfigurationService configurationService = environment.getInjector()
                .getInstance(ConfigurationService.class);
            boolean anvilProxyMode = configurationService.getOrDefault(Keys.PROXY_MODE);

            if (serverProxyMode && !anvilProxyMode) {
                getLogger().log(
                    Level.SEVERE,
                    "It looks like you are running this server behind a proxy. " +
                        "If this is the case, set server.proxyMode=true in the anvil config"
                );
            } else if (anvilProxyMode && !serverProxyMode) {
                getLogger().log(
                    Level.SEVERE,
                    "It looks like you are not running this server behind a proxy. " +
                        "If this is the case, set server.proxyMode=false in the anvil config"
                );
            }
        }
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onLoad(ServerLoadEvent event) {
                EnvironmentBuilderImpl.completeInitialization(new ApiSpigotModule());
            }
        }, this);
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}
