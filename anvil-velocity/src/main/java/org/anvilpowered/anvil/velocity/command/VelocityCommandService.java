/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.velocity.command;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.TextComponent;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.command.CommonCommandService;

import java.util.function.Predicate;

public class VelocityCommandService extends CommonCommandService<Command, Command, TextComponent, CommandSource> {

    private static class WrapperCommand implements Command {
        private final CommandExecutorWrapper<Command, CommandSource> wrapper;

        public WrapperCommand(CommandExecutorWrapper<Command, CommandSource> wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public void execute(CommandSource source, String[] args) {
            wrapper.execute(null, source, null, args);
        }
    }

    private abstract static class VCommand implements Command {

        protected final String helpUsage;
        protected final Predicate<CommandSource> extended;

        protected VCommand(String helpUsage, Predicate<CommandSource> extended) {
            this.helpUsage = helpUsage;
            this.extended = extended;
        }
    }

    private class RootCommand extends VCommand {
        public RootCommand(String helpUsage, Predicate<CommandSource> extended) {
            super(helpUsage, extended);
        }

        @Override
        public void execute(CommandSource source, String[] args) {
            sendRoot(source, helpUsage, extended.test(source));
        }
    }

    private class VersionCommand extends VCommand {

        public VersionCommand(String helpUsage, Predicate<CommandSource> extended) {
            super(helpUsage, extended);
        }

        @Override
        public void execute(CommandSource source, String[] args) {
            sendVersion(source, helpUsage, extended.test(source));
        }
    }

    private class HelpCommand implements Command {

        private final CommandNode<CommandSource> node;

        private HelpCommand(CommandNode<CommandSource> node) {
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
    protected void runExecutor(
        Command executor,
        Command command,
        CommandSource source,
        String alias,
        String[] context
    ) {
        executor.execute(source, context);
    }

    @Override
    protected Command generateWrapperCommand(CommandExecutorWrapper<Command, CommandSource> command) {
        return new WrapperCommand(command);
    }

    @Override
    public Command generateRootCommand(String helpUsage, Predicate<CommandSource> extended) {
        return new RootCommand(helpUsage, extended);
    }

    @Override
    public Command generateVersionCommand(String helpUsage, Predicate<CommandSource> extended) {
        return new VersionCommand(helpUsage, extended);
    }

    @Override
    public Command generateHelpCommand(CommandNode<CommandSource> node) {
        return new HelpCommand(node);
    }

    @Override
    public Command generateReloadCommand() {
        return new ReloadCommand();
    }
}
