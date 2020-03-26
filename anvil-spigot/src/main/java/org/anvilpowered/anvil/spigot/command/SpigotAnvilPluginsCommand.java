package org.anvilpowered.anvil.spigot.command;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.common.command.CommonAnvilPluginsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SpigotAnvilPluginsCommand
    extends CommonAnvilPluginsCommand<TextComponent, CommandSender>
    implements CommandExecutor {

    @Override
    public boolean onCommand(
        CommandSender source,
        Command command,
        String alias,
        String[] context
    ) {
        sendPlugins(source);
        return true;
    }
}
