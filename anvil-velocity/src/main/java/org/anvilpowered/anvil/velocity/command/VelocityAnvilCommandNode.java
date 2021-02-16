/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.anvilpowered.anvil.api.event.Event;
import org.anvilpowered.anvil.api.event.EventManager;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.common.MyEvent;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.velocity.command.regedit.VelocityRegistryEditCommandNode;
import org.anvilpowered.anvil.velocity.command.regedit.VelocityRegistryEditRootCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

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
    private VelocityCallbackCommand callbackCommand;

    @Inject
    private VelocityRegistryEditRootCommand registryEditRootCommand;

    @Inject
    private VelocityRegistryEditCommandNode registryEditCommandNode;

    @Inject
    private ProxyServer proxyServer;

    @Inject
    private EventManager eventManager;

    @Inject
    private Logger logger;

    private static final String HELP_COMMAND_PROXY = "/anvilv help";

    @Inject
    public VelocityAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {
        Map<List<String>, Command> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, anvilPluginsCommand);
        subCommands.put(RELOAD_ALIAS, anvilReloadCommand);
        subCommands.put(REGEDIT_ALIAS, commandService.generateRoutingCommand(
            registryEditRootCommand, registryEditCommandNode.getSubCommands(), false)
        );
        subCommands.put(HELP_ALIAS, commandService.generateHelpCommand(this));
        subCommands.put(VERSION_ALIAS, commandService.generateVersionCommand(HELP_COMMAND_PROXY));

        proxyServer.getCommandManager().register("anvilv",
            commandService.generateRoutingCommand(
                commandService.generateRootCommand(HELP_COMMAND_PROXY), subCommands, false),
            "av", "anvilv:av", "anvilv:anvilv");

        proxyServer.getCommandManager().register("callback", callbackCommand, "anvilv:callback");

        proxyServer.getCommandManager().register("testevent", new Command() {
          @Override
          public void execute(CommandSource source, String @NonNull [] args) {
            source.sendMessage(Identity.nil(), Component.text("In command"));

            eventManager.post(Event.class, new MyEvent("foo")).thenAccept(result -> {
                logger.info("Hello there: {}", result);
            });
          }
        });
    }
}
