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

package org.anvilpowered.anvil.bungee.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.bungee.AnvilBungee;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.common.command.CommonAnvilPluginsCommand;
import org.anvilpowered.anvil.common.command.CommonAnvilReloadCommand;
import org.anvilpowered.anvil.common.command.CommonCallbackCommand;

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

    @Inject
    private CommonCallbackCommand<TextComponent, CommandSender> callbackCommand;

    @Inject
    private AnvilBungee plugin;

    private static final String HELP_COMMAND_PROXY = "/anvilb help";
    private static final String ROOT_COMMAND_PROXY = "anvilb";

    @Inject
    public BungeeAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, BiConsumer<CommandSender, String[]>> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, (source, context) -> anvilPluginsCommand.execute(source));
        subCommands.put(RELOAD_ALIAS, anvilReloadCommand::execute);
        subCommands.put(HELP_ALIAS, commandService.generateHelpCommand(this));
        subCommands.put(VERSION_ALIAS, commandService.generateVersionCommand(HELP_COMMAND_PROXY));

        BiConsumer<CommandSender, String[]> root = commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND_PROXY), subCommands, false);

        ProxyServer.getInstance().getPluginManager()
            .registerCommand(plugin,
                new Command("anvilb", null, "ab", "anvilb:ab", "anvilb:anvilb") {
                    @Override
                    public void execute(CommandSender source, String[] context) {
                        root.accept(source, context);
                    }
                }
            );

        ProxyServer.getInstance().getPluginManager()
            .registerCommand(plugin,
                new Command("callback", null, "anvilb:callback") {
                    @Override
                    public void execute(CommandSender source, String[] context) {
                        callbackCommand.execute(source, context);
                    }
                }
            );
    }

    @Override
    public String getName() {
        return ROOT_COMMAND_PROXY;
    }
}
