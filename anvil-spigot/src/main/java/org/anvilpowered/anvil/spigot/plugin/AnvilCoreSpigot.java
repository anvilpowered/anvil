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
import org.anvilpowered.anvil.api.data.config.ConfigurationService;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.anvil.common.plugin.AnvilCore;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.spigot.listener.SpigotPlayerListener;
import org.anvilpowered.anvil.spigot.module.ApiSpigotModule;
import org.anvilpowered.anvil.spigot.module.SpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;

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
        private JavaPlugin plugin;

        @Inject
        public Inner(Injector injector) {
            super(injector, new SpigotModule());
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
                plugin.getLogger().log(
                    Level.SEVERE,
                    "It looks like you are running this server behind a proxy. " +
                        "If this is the case, set server.proxyMode=true in the anvil config"
                );
            } else if (anvilProxyMode && !serverProxyMode) {
                plugin.getLogger().log(
                    Level.SEVERE,
                    "It looks like you are not running this server behind a proxy. " +
                        "If this is the case, set server.proxyMode=false in the anvil config"
                );
            }
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
