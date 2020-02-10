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

package org.anvilpowered.anvil.core.spigot.plugin;

import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.Plugin;
import org.anvilpowered.anvil.core.common.plugin.AnvilCore;
import org.anvilpowered.anvil.core.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.core.spigot.listeners.SpigotPlayerListener;
import org.anvilpowered.anvil.core.spigot.module.SpigotModule;
import org.anvilpowered.anvil.spigot.module.ApiSpigotModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class AnvilCoreSpigot extends JavaPlugin implements Plugin<AnvilCoreSpigot> {

    protected final InnerAnvilCoreSpigot anvilCoreSpigot;

    public AnvilCoreSpigot() {
        anvilCoreSpigot = new InnerAnvilCoreSpigot();
    }

    private static final class InnerAnvilCoreSpigot extends AnvilCore<AnvilCoreSpigot> {
        public InnerAnvilCoreSpigot() {
            super(null, new SpigotModule());
        }
    }

    @Override
    public void onEnable() {
        Anvil.completeInitialization(new ApiSpigotModule());
        Bukkit.getPluginManager().registerEvents(anvilCoreSpigot.getPrimaryEnvironment().getInjector().getInstance(SpigotPlayerListener.class), this);
    }

    @Override
    public String toString() {
        return AnvilCorePluginInfo.id;
    }

    @Override
    public AnvilCoreSpigot getPluginContainer() {
        return this;
    }

    @Override
    public Environment getPrimaryEnvironment() {
        return anvilCoreSpigot.getPrimaryEnvironment();
    }

    @Override
    public Set<Environment> getAllEnvironments() {
        return anvilCoreSpigot.getAllEnvironments();
    }
}
