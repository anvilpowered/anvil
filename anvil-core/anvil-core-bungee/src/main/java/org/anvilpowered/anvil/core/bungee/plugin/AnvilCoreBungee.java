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

package org.anvilpowered.anvil.core.bungee.plugin;

import net.md_5.bungee.api.plugin.Plugin;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.bungee.module.ApiBungeeModule;
import org.anvilpowered.anvil.core.bungee.listeners.BungeePlayerListener;
import org.anvilpowered.anvil.core.bungee.module.BungeeModule;
import org.anvilpowered.anvil.core.common.plugin.AnvilCore;
import org.anvilpowered.anvil.core.common.plugin.AnvilCorePluginInfo;

import java.util.Set;

public class AnvilCoreBungee extends Plugin implements org.anvilpowered.anvil.api.plugin.Plugin<AnvilCoreBungee> {

    protected final InnerAnvilCoreBungee anvilCoreBungee;

    public AnvilCoreBungee() {
        anvilCoreBungee = new InnerAnvilCoreBungee();
    }

    private static final class InnerAnvilCoreBungee extends AnvilCore<AnvilCoreBungee> {
        public InnerAnvilCoreBungee() {
            super(null, new BungeeModule());
        }
    }

    @Override
    public void onEnable() {
        Anvil.completeInitialization(new ApiBungeeModule());
        getProxy().getPluginManager().registerListener(this, anvilCoreBungee.getPrimaryEnvironment().getInjector().getInstance(BungeePlayerListener.class));
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
    public AnvilCoreBungee getPluginContainer() {
        return this;
    }

    @Override
    public Environment getPrimaryEnvironment() {
        return anvilCoreBungee.getPrimaryEnvironment();
    }

    @Override
    public Set<Environment> getAllEnvironments() {
        return anvilCoreBungee.getAllEnvironments();
    }
}
