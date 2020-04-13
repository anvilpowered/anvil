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

package org.anvilpowered.anvil.velocity.command;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VelocityAnvilCommandNode
    extends CommonAnvilCommandNode<Command, CommandSource> {

    @Inject
    private VelocityAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private VelocityAnvilReloadCommand anvilReloadCommand;

    @Inject
    private ProxyServer proxyServer;

    private static final String HELP_COMMAND_PROXY = "/anvilv help";
    private static final String ROOT_COMMAND_PROXY = "anvilv";

    @Inject
    public VelocityAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, Command> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, anvilPluginsCommand);
        subCommands.put(RELOAD_ALIAS, anvilReloadCommand);
        subCommands.put(HELP_ALIAS, commandService.generateHelpCommand(this));
        subCommands.put(VERSION_ALIAS, commandService.generateVersionCommand(HELP_COMMAND_PROXY));

        proxyServer.getCommandManager().register(AnvilCorePluginInfo.id + "v",
            commandService.generateRoutingCommand(
                commandService.generateRootCommand(HELP_COMMAND_PROXY), subCommands, false),
            "av");
    }

    @Override
    public String getName() {
        return ROOT_COMMAND_PROXY;
    }
}
