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

package org.anvilpowered.anvil.sponge.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.anvilpowered.anvil.api.core.data.key.AnvilCoreKeys;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

@Singleton
public class AnvilSpongeCommandNode implements CommandNode<CommandSpec> {

    private boolean alreadyLoaded;
    private CommandSpec command;
    private Map<List<String>, CommandSpec> subCommands;
    private Map<List<String>, String> commandDescriptions;
    private Map<List<String>, String> commandUsage;
    private Map<List<String>, Predicate<Object>> commandPermissions;
    Map<List<String>, Function<Object, String>> usage;

    @Inject
    private AnvilSpongePluginsCommand anvilSpongePluginsCommand;

    @Inject
    private AnvilSpongeReloadCommand anvilSpongeReloadCommand;

    @Inject
    private CommandService<CommandSpec, CommandExecutor, CommandSource> commandService;

    @Inject
    private Environment environment;

    @Inject
    public AnvilSpongeCommandNode(Registry registry) {
        registry.addRegistryLoadedListener(this::registryLoaded);
        alreadyLoaded = false;
    }

    public void registryLoaded() {
        if (alreadyLoaded) return;
        alreadyLoaded = true;

        subCommands = new HashMap<>();
        commandDescriptions = new HashMap<>();
        commandUsage = new HashMap<>();
        commandPermissions = new HashMap<>();
        usage = new HashMap<>();

        List<String> pluginCommandAliases = Collections.singletonList("plugins");
        commandDescriptions.put(pluginCommandAliases, "List Anvil plugins");
        commandPermissions.put(Collections.singletonList("plugins"), c ->
            c instanceof CommandSource && subCommands.get(pluginCommandAliases).testPermission((CommandSource) c));
        subCommands.put(pluginCommandAliases, CommandSpec.builder()
            .description(Text.of(commandDescriptions.get(pluginCommandAliases)))
            .permission(AnvilCoreKeys.PLUGINS_PERMISSION.getFallbackValue())
            .executor(anvilSpongePluginsCommand)
            .build()
        );
        usage.put(pluginCommandAliases, c -> subCommands.get(pluginCommandAliases).getUsage((CommandSource) c).toPlain());

        List<String> reloadCommandAliases = Collections.singletonList("reload");
        commandDescriptions.put(reloadCommandAliases, "Reload Anvil plugins");
        commandUsage.put(reloadCommandAliases, "/anvil reload");
        commandPermissions.put(reloadCommandAliases, c ->
            c instanceof CommandSource && subCommands.get(reloadCommandAliases).testPermission((CommandSource) c));
        subCommands.put(reloadCommandAliases, CommandSpec.builder()
            .description(Text.of(commandDescriptions.get(reloadCommandAliases)))
            .permission(AnvilCoreKeys.RELOAD_PERMISSION.getFallbackValue())
            .arguments(
                GenericArguments.flags().flag("a", "-all").flag("r", "-regex")
                    .buildWith(GenericArguments.optional(
                        GenericArguments.string(Text.of("plugin"))))
            )
            .executor(anvilSpongeReloadCommand)
            .build()
        );
        usage.put(reloadCommandAliases, c ->
            subCommands.get(reloadCommandAliases).getUsage((CommandSource) c).toPlain());

        List<String> helpCommandAliases = Collections.singletonList("help");
        commandDescriptions.put(helpCommandAliases, "Help command");
        commandUsage.put(helpCommandAliases, "/anvil help");
        commandPermissions.put(helpCommandAliases, c -> c instanceof CommandSource && subCommands.get(helpCommandAliases).testPermission((CommandSource) c));
        subCommands.put(helpCommandAliases, CommandSpec.builder()
            .description(Text.of("Help command"))
            .executor(commandService.generateHelpCommand(this))
            .build()
        );
        usage.put(helpCommandAliases, c -> subCommands.get(helpCommandAliases).getUsage((CommandSource) c).toPlain());

        List<String> versionCommandAliases = Collections.singletonList("version");
        commandDescriptions.put(versionCommandAliases, "Version command");
        commandPermissions.put(versionCommandAliases, c -> c instanceof CommandSource && subCommands.get(versionCommandAliases).testPermission((CommandSource) c));
        subCommands.put(versionCommandAliases, CommandSpec.builder()
            .description(Text.of(commandDescriptions.get(versionCommandAliases)))
            .executor(commandService.generateVersionCommand("/anvil help"))
            .build()
        );
        usage.put(versionCommandAliases, c -> subCommands.get(versionCommandAliases).getUsage((CommandSource) c).toPlain());

        command = CommandSpec.builder()
            .description(Text.of("Root command"))
            .executor(commandService.generateRootCommand("/anvil help"))
            .children(subCommands)
            .build();

        Sponge.getCommandManager()
            .register(environment.getPlugin(), command, AnvilCorePluginInfo.id);
    }

    private static final String ERROR_MESSAGE = "Anvil command has not been loaded yet";

    @Override
    public Map<List<String>, String> getDescription() {
        return commandDescriptions;
    }

    @Override
    public Map<List<String>, Predicate<Object>> getPermissionsCheck() {
        return commandPermissions;
    }

    @Override
    public Map<List<String>, Function<Object, String>> getUsage() {
        return usage;
    }

    @Override
    public Map<List<String>, CommandSpec> getCommands() {
        return Objects.requireNonNull(subCommands, ERROR_MESSAGE);
    }

    @Override
    public CommandSpec getCommand() {
        return Objects.requireNonNull(command, ERROR_MESSAGE);
    }

    @Override
    public String getName() {
        return AnvilCorePluginInfo.id;
    }
}
