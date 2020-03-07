package org.anvilpowered.anvil.spigot.command;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.util.CommonCommandService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class SpigotCommandService extends CommonCommandService<Command, Command, TextComponent, CommandSender> {

    private static abstract class sCommand extends Command {
        protected final String helpCommandName;
        protected final Predicate<CommandSender> extended;

        public sCommand(String helpCommandName, Predicate<CommandSender> extended) {
            super(helpCommandName);
            this.helpCommandName = helpCommandName;
            this.extended = extended;
        }
    }

    private class RootCommand extends sCommand {

        public RootCommand(String helpCommandName, Predicate<CommandSender> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            sendRoot(sender, helpCommandName, extended.test(sender));
            return false;
        }
    }

    private class VersionCommand extends sCommand {

        public VersionCommand(String helpCommandName, Predicate<CommandSender> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            sendVersion(sender, helpCommandName, extended.test(sender));
            return true;
        }
    }

    private class HelpCommand extends Command {

        private final CommandNode<Command> node;

        public HelpCommand(CommandNode<Command> node) {
            super("help");
            this.node = node;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            sendHelp(sender, node);
            return false;
        }
    }

    private class ReloadCommand extends Command {

        protected ReloadCommand() {
            super("reload");
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
            sendReload(sender);
            return true;
        }
    }

    @Override
    public void registerCommand(List<String> aliases, Command command, CommandNode<Command> node) {
        node.getCommands().put(aliases, command);
        node.getDescriptions().put(aliases,
            c -> c instanceof CommandSender
                ? command.getDescription()
                : "null"
        );
        node.getPermissions().put(aliases,
            c -> c instanceof CommandSender && command.testPermission((CommandSender) c)
        );
        node.getUsages().put(aliases,
            c -> c instanceof CommandSender
                ? command.getUsage()
                : "null"
        );
    }

    @Override
    public Command generateRootCommand(String helpCommandName, Predicate<CommandSender> extended) {
        return new RootCommand(helpCommandName, extended);
    }

    @Override
    public Command generateVersionCommand(String helpCommandName, Predicate<CommandSender> extended) {
        return new VersionCommand(helpCommandName, extended);
    }

    @Override
    public Command generateHelpCommand(CommandNode<Command> node) {
        return new HelpCommand(node);
    }

    @Override
    public Command generateReloadCommand() {
        return new ReloadCommand();
    }
}
