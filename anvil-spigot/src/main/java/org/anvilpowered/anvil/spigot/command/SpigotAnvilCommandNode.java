package org.anvilpowered.anvil.spigot.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.common.command.CommonAnvilCommandNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpigotAnvilCommandNode
    extends CommonAnvilCommandNode<CommandExecutor, CommandSender> {

    @Inject
    private SpigotAnvilPluginsCommand anvilPluginsCommand;

    @Inject
    private SpigotAnvilReloadCommand anvilReloadCommand;

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

        PluginCommand root = environment.<JavaPlugin>getPlugin()
            .getPluginContainer().getCommand(getName());

        Objects.requireNonNull(root, "Anvil command not registered");

        root.setExecutor(commandService.generateRoutingCommand(
            commandService.generateRootCommand(HELP_COMMAND), subCommands, false));
    }
}
