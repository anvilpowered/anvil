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
import org.anvilpowered.anvil.api.core.data.key.AnvilCoreKeys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
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
    private Map<List<String>, Function<Object, String>> descriptions;
    private Map<List<String>, Predicate<Object>> permissions;
    private Map<List<String>, Function<Object, String>> usages;

    @Inject
    private SpongeAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private SpongeAnvilReloadCommand anvilReloadCommand;

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
        descriptions = new HashMap<>();
        usages = new HashMap<>();
        permissions = new HashMap<>();

        commandService.registerCommand(
            Collections.singletonList("plugins"),
            CommandSpec.builder()
                .description(Text.of("List plugins"))
                .permission(AnvilCoreKeys.PLUGINS_PERMISSION.getFallbackValue())
                .executor(anvilPluginsCommand)
                .build(),
            this
        );

        commandService.registerCommand(
            Collections.singletonList("reload"),
            CommandSpec.builder()
                .description(Text.of("Reloads plugins"))
                .permission(AnvilCoreKeys.RELOAD_PERMISSION.getFallbackValue())
                .arguments(
                    GenericArguments.flags().flag("a", "-all").flag("r", "-regex")
                        .buildWith(GenericArguments.optional(
                            GenericArguments.string(Text.of("plugin"))))
                )
                .executor(anvilReloadCommand)
                .build(),
            this
        );

        commandService.registerCommand(
            Collections.singletonList("help"),
            CommandSpec.builder()
                .description(Text.of("Help command"))
                .executor(commandService.generateHelpCommand(this))
                .build(),
            this
        );

        commandService.registerCommand(
            Collections.singletonList("version"),
            CommandSpec.builder()
                .description(Text.of("Version command"))
                .executor(commandService.generateVersionCommand("/anvil help"))
                .build(),
            this
        );

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
    public String getName() {
        return AnvilCorePluginInfo.id;
    }

    @Override
    public CommandSpec getCommand() {
        return Objects.requireNonNull(command, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, CommandSpec> getCommands() {
        return Objects.requireNonNull(subCommands, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Function<Object, String>> getDescriptions() {
        return Objects.requireNonNull(descriptions, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Predicate<Object>> getPermissions() {
        return Objects.requireNonNull(permissions, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Function<Object, String>> getUsages() {
        return Objects.requireNonNull(usages, ERROR_MESSAGE);
    }
}
