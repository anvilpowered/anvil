package org.anvilpowered.anvil.spigot.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.common.command.CommonAnvilReloadCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpigotAnvilReloadCommand extends Command {

    protected final Inner inner;

    @Inject
    protected SpigotAnvilReloadCommand(Inner inner) {
        super("reload");
        this.inner = inner;
    }

    private static class Inner extends CommonAnvilReloadCommand<TextComponent, CommandSender> {
        private String all() {
            return doAll();
        }

        private boolean check(CommandSender sender, boolean present) {
            return checkPresent(sender, present);
        }

        private boolean regex(CommandSender sender, String plugin, String[] reloadedResult) {
            return doRegex(sender, plugin, reloadedResult);
        }

        private boolean direct(CommandSender sender, String plugin, String[] reloadedResult) {
            return doDirect(sender, plugin, reloadedResult);
        }

        private void success(CommandSender sender, String[] reloadedResult) {
            sendSuccess(sender, reloadedResult);
        }
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Optional<String> optionalPlugin;

        if (args[0].equalsIgnoreCase("reload") && args[1] != null) {
            optionalPlugin = Optional.of(args[1]);
        } else {
            return false;
        }

        String[] reloadedResult = {""};
        if (args[1].equalsIgnoreCase("a")) {
            reloadedResult[0] = inner.all();
        } else if (!inner.check(sender, optionalPlugin.isPresent())) {
            return false;
        } else if (args[1].equalsIgnoreCase("r")) {
            if (!inner.regex(sender, optionalPlugin.get(), reloadedResult)) {
                return false;
            }
        } else if (!inner.direct(sender, optionalPlugin.get(), reloadedResult)) {
            return false;
        }
        inner.success(sender, reloadedResult);
        return true;
    }
}
