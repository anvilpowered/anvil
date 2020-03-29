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

package org.anvilpowered.anvil.bungee.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.common.command.CommonAnvilPluginsCommand;
import org.anvilpowered.anvil.common.command.CommonAnvilReloadCommand;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BungeeAnvilCommandNode
    extends CommonAnvilCommandNode<BiConsumer<CommandSender, String[]>, CommandSender> {

    @Inject
    private CommonAnvilPluginsCommand<TextComponent, CommandSender> anvilPluginsCommand;

    @Inject
    private CommonAnvilReloadCommand<TextComponent, CommandSender> anvilReloadCommand;

    private static final String HELP_COMMAND_PROXY = "/anvilb help";

    @Inject
    public BungeeAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, BiConsumer<CommandSender, String[]>> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, (source, context) -> anvilPluginsCommand.sendPlugins(source));
        subCommands.put(RELOAD_ALIAS, anvilReloadCommand::sendReload);
        subCommands.put(HELP_ALIAS, commandService.generateHelpCommand(this));
        subCommands.put(VERSION_ALIAS, commandService.generateVersionCommand(HELP_COMMAND_PROXY));

        BiConsumer<CommandSender, String[]> root = commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND_PROXY), subCommands, false);

        ProxyServer.getInstance().getPluginManager()
            .registerCommand(
                environment.<Plugin>getPlugin().getPluginContainer(),
                new Command(AnvilCorePluginInfo.id + "b", null, "ab") {
                    @Override
                    public void execute(CommandSender sender, String[] args) {
                        root.accept(sender, args);
                    }
                }
            );
    }
}
