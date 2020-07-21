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

package org.anvilpowered.anvil.sponge.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.core.data.key.AnvilCoreKeys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.anvilpowered.anvil.common.plugin.AnvilPluginInfo;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class SpongeAnvilCommandNode
    extends CommonAnvilCommandNode<CommandExecutor, CommandSource> {

    @Inject
    private SpongeAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private SpongeAnvilReloadCommand anvilReloadCommand;

    @Inject
    public SpongeAnvilCommandNode(Registry registry) {
        super(registry);
    }

    @Override
    protected void loadCommands() {

        Map<List<String>, CommandCallable> subCommands = new HashMap<>();

        subCommands.put(PLUGINS_ALIAS, CommandSpec.builder()
            .description(Text.of(PLUGINS_DESCRIPTION))
            .permission(registry.getOrDefault(AnvilCoreKeys.PLUGINS_PERMISSION))
            .executor(anvilPluginsCommand)
            .build());

        subCommands.put(RELOAD_ALIAS, CommandSpec.builder()
            .description(Text.of(RELOAD_DESCRIPTION))
            .permission(registry.getOrDefault(AnvilCoreKeys.RELOAD_PERMISSION))
            .arguments(
                GenericArguments.flags().flag("a", "-all").flag("r", "-regex")
                    .buildWith(GenericArguments.optional(
                        GenericArguments.withSuggestions(
                            GenericArguments.string(Text.of("plugin")),
                            anvilReloadCommand.suggest()
                        )
                    ))
            )
            .executor(anvilReloadCommand)
            .build());

        subCommands.put(HELP_ALIAS, CommandSpec.builder()
            .description(Text.of(HELP_DESCRIPTION))
            .executor(commandService.generateHelpCommand(this))
            .build());

        subCommands.put(VERSION_ALIAS, CommandSpec.builder()
            .description(Text.of(VERSION_DESCRIPTION))
            .executor(commandService.generateVersionCommand(HELP_COMMAND))
            .build());

        CommandSpec root = CommandSpec.builder()
            .description(Text.of(ROOT_DESCRIPTION))
            .executor(commandService.generateRootCommand(HELP_COMMAND))
            .children(subCommands)
            .build();

        Sponge.getCommandManager()
            .register(environment.getPlugin(), root, AnvilPluginInfo.id);
    }
}
