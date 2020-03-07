package org.anvilpowered.anvil.velocity.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.util.CommonCommandService;

import java.util.List;
import java.util.function.Predicate;

public class VelocityCommandService extends CommonCommandService<Command, Command, TextComponent, CommandSource> {

    private abstract static class VCommand implements Command {

        protected final String helpCommandName;
        protected final Predicate<CommandSource> extended;

        protected VCommand(String helpCommandName, Predicate<CommandSource> extended) {
            this.helpCommandName = helpCommandName;
            this.extended = extended;
        }
    }

    private class RootCommand extends VCommand {
        public RootCommand(String helpCommandName, Predicate<CommandSource> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public void execute(CommandSource source, String[] args) {
            sendRoot(source, helpCommandName, extended.test(source));
        }
    }

    private class VersionCommand extends VCommand {

        public VersionCommand(String helpCommandName, Predicate<CommandSource> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public void execute(CommandSource source, String[] args) {
            sendVersion(source, helpCommandName, extended.test(source));
        }
    }

    private class HelpCommand implements Command {

        private final CommandNode<Command> node;

        private HelpCommand(CommandNode<Command> node) {
            this.node = node;
        }

        @Override
        public void execute(CommandSource source, String[] args) {
            sendHelp(source, node);
        }
    }

    private class ReloadCommand implements Command {

        @Override
        public void execute(CommandSource source, String[] args) {
            sendReload(source);
        }
    }

    @Override
    public void registerCommand(List<String> aliases, Command command, CommandNode<Command> node) {

    }

    @Override
    public Command generateRootCommand(String helpCommandName, Predicate<CommandSource> extended) {
        return new RootCommand(helpCommandName, extended);
    }

    @Override
    public Command generateVersionCommand(String helpCommandName, Predicate<CommandSource> extended) {
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
