package org.anvilpowered.anvil.spigot.command;

import net.md_5.bungee.api.chat.TextComponent;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.command.CommonCommandService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.function.Predicate;

public class SpigotCommandService extends CommonCommandService<Command, CommandExecutor, TextComponent, CommandSender> {

    private static class WrapperCommand implements CommandExecutor {
        private final CommandExecutorWrapper<Command, CommandSender> wrapper;

        public WrapperCommand(CommandExecutorWrapper<Command, CommandSender> wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public boolean onCommand(CommandSender source, Command command, String label, String[] args) {
            wrapper.execute(command, source, label, args);
            return true;
        }
    }

    private static abstract class SCommand implements CommandExecutor {
        protected final String helpUsage;
        protected final Predicate<CommandSender> extended;

        public SCommand(String helpUsage, Predicate<CommandSender> extended) {
            this.helpUsage = helpUsage;
            this.extended = extended;
        }
    }

    private class RootCommand extends SCommand {

        public RootCommand(String helpUsage, Predicate<CommandSender> extended) {
            super(helpUsage, extended);
        }

        @Override
        public boolean onCommand(CommandSender source, Command command, String label, String[] args) {
            sendRoot(source, helpUsage, extended.test(source));
            return true;
        }
    }

    private class VersionCommand extends SCommand {

        public VersionCommand(String helpUsage, Predicate<CommandSender> extended) {
            super(helpUsage, extended);
        }

        @Override
        public boolean onCommand(CommandSender source, Command command, String label, String[] args) {
            sendVersion(source, helpUsage, extended.test(source));
            return true;
        }
    }

    private class HelpCommand implements CommandExecutor {

        private final CommandNode<CommandSender> node;

        public HelpCommand(CommandNode<CommandSender> node) {
            this.node = node;
        }

        @Override
        public boolean onCommand(CommandSender source, Command command, String label, String[] args) {
            sendHelp(source, node);
            return true;
        }
    }

    private class ReloadCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender source, Command command, String label, String[] args) {
            sendReload(source);
            return true;
        }
    }

    @Override
    protected void runExecutor(
        CommandExecutor executor,
        Command command,
        CommandSender source,
        String alias,
        String[] context
    ) {
        executor.onCommand(source, command, alias, context);
    }

    @Override
    protected CommandExecutor generateWrapperCommand(CommandExecutorWrapper<Command, CommandSender> command) {
        return new WrapperCommand(command);
    }

    @Override
    public CommandExecutor generateRootCommand(String helpUsage, Predicate<CommandSender> extended) {
        return new RootCommand(helpUsage, extended);
    }

    @Override
    public CommandExecutor generateVersionCommand(String helpUsage, Predicate<CommandSender> extended) {
        return new VersionCommand(helpUsage, extended);
    }

    @Override
    public CommandExecutor generateHelpCommand(CommandNode<CommandSender> node) {
        return new HelpCommand(node);
    }

    @Override
    public CommandExecutor generateReloadCommand() {
        return new ReloadCommand();
    }
}
