package org.anvilpowered.anvil.velocity.command;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.text.TextComponent;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.common.util.CommonCommandService;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class VelocityCommandService extends CommonCommandService<Command, Command, TextComponent, CommandSource> {

    @Inject
    private TextService<TextComponent, CommandSource> textService;

    private abstract class VCommand implements com.velocitypowered.api.command.Command {
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
        public void execute(CommandSource source, @NonNull String[] args) {
            sendRoot(source, helpCommandName, extended.test(source));
        }
    }

    private class VersionCommand extends VCommand {

        public VersionCommand(String helpCommandName, Predicate<CommandSource> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public void execute(CommandSource source, @NonNull String[] args) {
            sendVersion(source, helpCommandName, extended.test(source));
        }
    }
    private class HelpCommand implements Command {

        private final CommandNode<Command> node;

        private HelpCommand(CommandNode<Command> node) {
            this.node = node;
        }

        @Override
        public void execute(CommandSource source, @NonNull String[] args) {
            List<TextComponent> helpList = new ArrayList<>();
            String fullPath = getFullPath(node);
            node.getCommands().forEach((aliases, command) -> {

                String subCommand = aliases.toString().replace("[", "").replace("]", "");
                 helpList.add(textService.builder()
                    .gold().append(fullPath, subCommand)
                    .gold().append(" - " + node.getDescription().get(aliases) + "\n")
                    .gray().append("Usage: ", fullPath, subCommand, " ", "Usage...")
                    .build()
                );
            });
            //TODO implement Pagination
        }
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
        return null;
    }
}
