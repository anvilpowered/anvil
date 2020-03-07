package org.anvilpowered.anvil.spigot.command;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.core.data.key.AnvilCoreKeys;
import org.anvilpowered.anvil.common.command.CommonAnvilPluginsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SpigotAnvilPluginsCommand extends Command {

    protected final Inner inner;

    public SpigotAnvilPluginsCommand(String name) {
        super(name);
        this.setDescription("Anvil Plugins Command");
        this.setUsage("/anvil plugins");
        this.setPermission(AnvilCoreKeys.PLUGINS_PERMISSION.getFallbackValue());
        this.setAliases(Collections.singletonList("anvil"));
        inner = new Inner();
    }

    private static class Inner
        extends CommonAnvilPluginsCommand<TextComponent, CommandSender> {
        private void sendList(CommandSender sender) {
            sendPlugins(sender);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender,
                           @NotNull String commandLabel,
                           @NotNull String[] args) {
        inner.sendList(sender);
        return true;
    }
}
