/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020 Cableguy20
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.sponge.command;

import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.util.CommonCommandService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Predicate;

public class SpongeCommandService extends CommonCommandService<CommandSpec, CommandExecutor, Text, CommandSource> {

    private static abstract class Command implements CommandExecutor {

        protected final String helpCommandName;
        protected final Predicate<CommandSource> extended;

        public Command(String helpCommandName, Predicate<CommandSource> extended) {
            this.helpCommandName = helpCommandName;
            this.extended = extended;
        }
    }

    private class RootCommand extends Command {

        public RootCommand(String helpCommandName, Predicate<CommandSource> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) {
            sendRoot(source, helpCommandName, extended.test(source));
            return CommandResult.success();
        }
    }

    private class VersionCommand extends Command {

        public VersionCommand(String helpCommandName, Predicate<CommandSource> extended) {
            super(helpCommandName, extended);
        }

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) {
            sendVersion(source, helpCommandName, extended.test(source));
            return CommandResult.success();
        }
    }

    private class HelpCommand implements CommandExecutor {

        private final CommandNode<CommandSpec> node;

        public HelpCommand(CommandNode<CommandSpec> node) {
            this.node = node;
        }

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
            sendHelp(source, node);
            return CommandResult.success();
        }
    }


    private class ReloadCommand implements CommandExecutor {

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) {
            sendReload(source);
            return CommandResult.success();
        }
    }

    @Override
    public void registerCommand(List<String> aliases, CommandSpec commandSpec, CommandNode<CommandSpec> node) {
        node.getCommands().put(aliases, commandSpec);
        node.getDescriptions().put(aliases,
            c -> c instanceof CommandSource
                ? commandSpec.getShortDescription((CommandSource) c)
                .map(Text::toPlain).orElse("null")
                : "null"
        );
        node.getPermissions().put(aliases,
            c -> c instanceof CommandSource && commandSpec.testPermission((CommandSource) c)
        );
        node.getUsages().put(aliases,
            c -> c instanceof CommandSource
                ? commandSpec.getUsage((CommandSource) c).toPlain()
                : "null"
        );
    }

    @Override
    public CommandExecutor generateRootCommand(String helpCommandName, Predicate<CommandSource> extended) {
        return new RootCommand(helpCommandName, extended);
    }

    @Override
    public CommandExecutor generateVersionCommand(String helpCommandName, Predicate<CommandSource> extended) {
        return new VersionCommand(helpCommandName, extended);
    }

    @Override
    public CommandExecutor generateHelpCommand(CommandNode<CommandSpec> node) {
        return new HelpCommand(node);
    }

    @Override
    public CommandExecutor generateReloadCommand() {
        return new ReloadCommand();
    }
}
