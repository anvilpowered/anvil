package org.anvilpowered.anvil.spigot.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.plugin.AnvilCorePluginInfo;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class SpigotAnvilCommandNode implements CommandNode<Command> {

    private boolean alreadyLoaded;
    private Command command;
    private Map<List<String>, Command> subCommands;
    private Map<List<String>, Function<Object, String>> descriptions;
    private Map<List<String>, Predicate<Object>> permissions;
    private Map<List<String>, Function<Object, String>> usages;

    @Inject
    private SpigotAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private SpigotAnvilReloadCommand anvilReloadCommand;

    @Inject
    private CommandService<Command, Command, CommandSender> commandService;

    @Inject
    public SpigotAnvilCommandNode(Registry registry) {
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
            anvilPluginsCommand,
            this
        );

        commandService.registerCommand(
            Collections.singletonList("reload"),
            anvilReloadCommand,
            this
        );

        commandService.registerCommand(
            Collections.singletonList("help"),
            commandService.generateHelpCommand(this),
            this
        );

        command = new Command("help", "root command", "/anvil help", Arrays.asList("h")) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                commandService.generateRootCommand("/anvil help");
                return true;
            }
        };
    }

    private static final String ERROR_MESSAGE = "Anvil command has not been loaded yet";

    @Override
    public Command getCommand() {
        return Objects.requireNonNull(command, ERROR_MESSAGE);
    }

    @Override
    public Map<List<String>, Command> getCommands() {
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

    @Override
    public String getName() {
        return AnvilCorePluginInfo.id;
    }
}
