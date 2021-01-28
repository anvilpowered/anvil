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

package org.anvilpowered.anvil.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import com.google.common.collect.ImmutableList;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.command.CommonCommandService;

import java.util.List;
import java.util.function.Predicate;

public class NukkitCommandService extends CommonCommandService<Command, CommandExecutor, String, CommandSender> {

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
    protected List<String> getSuggestions(
        CommandExecutor commandExecutor,
        Command command,
        CommandSender commandSender,
        String alias,
        String[] context
    ) {
        return ImmutableList.of();
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
