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

package org.anvilpowered.anvil.spigot.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.AnvilImpl;
import org.anvilpowered.anvil.api.command.CommandExecuteService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SpigotCommandExecuteService implements CommandExecuteService {

    @Inject(optional = true)
    private Plugin plugin;

    private void executeDirect(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public void execute(String command) {
        if (Bukkit.isPrimaryThread()) {
            executeDirect(command);
        } else if (plugin != null) {
            Bukkit.getScheduler().runTask(plugin, () -> executeDirect(command));
        } else {
            AnvilImpl.getLogger()
                .error("You must bind org.bukkit.plugin.Plugin to your plugin instance to be able to run commands async");
        }
    }
}
