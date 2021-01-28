/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anvilpowered.anvil.sponge.command;

import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.command.CommonCommandService;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Predicate;

public class SpongeCommandService
    extends CommonCommandService<CommandCallable, CommandExecutor, Text, CommandSource> {

    private static abstract class Command implements CommandExecutor {

        protected final String helpUsage;
        protected final Predicate<CommandSource> extended;

        public Command(String helpUsage, Predicate<CommandSource> extended) {
            this.helpUsage = helpUsage;
            this.extended = extended;
        }
    }

    private class RootCommand extends Command {

        public RootCommand(String helpUsage, Predicate<CommandSource> extended) {
            super(helpUsage, extended);
        }

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) {
            sendRoot(source, helpUsage, extended.test(source));
            return CommandResult.success();
        }
    }

    private class VersionCommand extends Command {

        public VersionCommand(String helpUsage, Predicate<CommandSource> extended) {
            super(helpUsage, extended);
        }

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) {
            sendVersion(source, helpUsage, extended.test(source));
            return CommandResult.success();
        }
    }

    private class HelpCommand implements CommandExecutor {

        private final CommandNode<CommandSource> node;

        public HelpCommand(CommandNode<CommandSource> node) {
            this.node = node;
        }

        @Override
        public CommandResult execute(CommandSource source, CommandContext context) {
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
    protected void runExecutor(
        CommandExecutor commandExecutor,
        CommandCallable commandCallable,
        CommandSource commandSource,
        String alias,
        String[] context
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<String> getSuggestions(
        CommandExecutor commandExecutor,
        CommandCallable commandCallable,
        CommandSource commandSource,
        String alias,
        String[] context
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected CommandExecutor generateWrapperCommand(
        CommandExecutorWrapper<CommandCallable, CommandSource> command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandExecutor generateRootCommand(
        String helpUsage, Predicate<CommandSource> extended) {
        return new RootCommand(helpUsage, extended);
    }

    @Override
    public CommandExecutor generateVersionCommand(
        String helpUsage, Predicate<CommandSource> extended) {
        return new VersionCommand(helpUsage, extended);
    }

    @Override
    public CommandExecutor generateHelpCommand(CommandNode<CommandSource> node) {
        return new HelpCommand(node);
    }

    @Override
    public CommandExecutor generateReloadCommand() {
        return new ReloadCommand();
    }
}
