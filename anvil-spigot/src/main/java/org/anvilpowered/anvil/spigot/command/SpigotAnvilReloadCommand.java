package org.anvilpowered.anvil.spigot.command;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.common.command.CommonAnvilReloadCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpigotAnvilReloadCommand
    extends CommonAnvilReloadCommand<TextComponent, CommandSender>
    implements CommandExecutor {

    @Override
    public boolean onCommand(
        CommandSender source,
        Command command,
        String alias,
        String[] context
    ) {
        sendReload(source, context);
        return true;
    }
}
