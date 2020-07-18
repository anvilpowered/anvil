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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.spigot.AnvilSpigot;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpigotAnvilCommandNode
    extends CommonAnvilCommandNode<CommandExecutor, CommandSender> {

    @Inject
    private SpigotAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private SpigotAnvilReloadCommand anvilReloadCommand;

    @Inject
    private SpigotCallbackCommand callbackCommand;

    @Inject
    private AnvilSpigot plugin;

    @Inject
    public SpigotAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, CommandExecutor> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, anvilPluginsCommand);
        subCommands.put(RELOAD_ALIAS, anvilReloadCommand);
        subCommands.put(HELP_ALIAS, commandService.generateHelpCommand(this));
        subCommands.put(VERSION_ALIAS, commandService.generateVersionCommand(HELP_COMMAND));

        PluginCommand root = plugin.getCommand(getName());

        Preconditions.checkNotNull(root, "Anvil command not registered");

        root.setExecutor(commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND), subCommands, false));

        PluginCommand callback = plugin.getCommand("callback");

        Preconditions.checkNotNull(callback, "Callback command not registered");

        callback.setExecutor(callbackCommand);
    }
}
