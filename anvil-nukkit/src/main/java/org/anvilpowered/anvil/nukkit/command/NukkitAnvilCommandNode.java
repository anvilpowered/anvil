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

package org.anvilpowered.anvil.nukkit.command;

import cn.nukkit.Server;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.Plugin;
import com.google.inject.Inject;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.nukkit.AnvilNukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NukkitAnvilCommandNode
    extends CommonAnvilCommandNode<CommandExecutor, CommandSender> {

    @Inject
    private NukkitAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private NukkitAnvilReloadCommand anvilReloadCommand;

    @Inject
    private AnvilNukkit plugin;

    @Inject
    public NukkitAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, CommandExecutor> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, anvilPluginsCommand);
        subCommands.put(RELOAD_ALIAS, anvilReloadCommand);
        subCommands.put(HELP_ALIAS, commandService.generateHelpCommand(this));
        subCommands.put(VERSION_ALIAS, commandService.generateVersionCommand(HELP_COMMAND));

        PluginCommand<Plugin> root = new PluginCommand<>(getName(), plugin);

        root.setExecutor(commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND), subCommands, false));

        Server.getInstance().getCommandMap().register(getName(), root);
    }
}
