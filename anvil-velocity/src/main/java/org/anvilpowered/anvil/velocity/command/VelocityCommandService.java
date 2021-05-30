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

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.TextComponent;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.command.CommonCommandService;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.function.Predicate;

public class VelocityCommandService extends CommonCommandService<SimpleCommand, SimpleCommand, TextComponent, CommandSource> {

    private static class WrapperCommand implements SimpleCommand {
        private final CommandExecutorWrapper<SimpleCommand, CommandSource> wrapper;

        public WrapperCommand(CommandExecutorWrapper<SimpleCommand, CommandSource> wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public void execute(Invocation invocation) {
            wrapper.execute(null, invocation.source(), null, invocation.arguments());
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            return wrapper.suggest(null, invocation.source(), null, invocation.arguments());
        }
    }

    private abstract static class VCommand implements SimpleCommand {

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
        public void execute(Invocation invocation) {
            sendRoot(invocation.source(), helpUsage, extended.test(invocation.source()));
        }
    }

    private class VersionCommand extends VCommand {

        public VersionCommand(String helpUsage, Predicate<CommandSource> extended) {
            super(helpUsage, extended);
        }

        @Override
        public void execute(Invocation invocation) {
            sendVersion(invocation.source(), helpUsage, extended.test(invocation.source()));
        }
    }

    private class HelpCommand implements SimpleCommand {

        private final CommandNode<CommandSource> node;

        private HelpCommand(CommandNode<CommandSource> node) {
            this.node = node;
        }

        @Override
        public void execute(Invocation invocation) {
            sendHelp(invocation.source(), node);
        }
    }

    private class ReloadCommand implements SimpleCommand {

        @Override
        public void execute(Invocation invocation) {
            sendReload(invocation.source());
        }
    }

    @Override
    protected void runExecutor(
        SimpleCommand executor,
        SimpleCommand command,
        CommandSource source,
        String alias,
        String[] context) {
        executor.execute(new SimpleCommand.Invocation() {
            @Override
            public String alias() {
                return alias;
            }

            @Override
            public CommandSource source() {
                return source;
            }

            @Override
            public String @NonNull [] arguments() {
                return context;
            }
        });
    }

    @Override
    protected List<String> getSuggestions(
        SimpleCommand executor,
        SimpleCommand command,
        CommandSource source,
        String alias,
        String[] context) {
        return executor.suggest(new SimpleCommand.Invocation() {
            @Override
            public String alias() {
                return alias;
            }

            @Override
            public CommandSource source() {
                return source;
            }

            @Override
            public String @NonNull [] arguments() {
                return context;
            }
        });
    }

    @Override
    protected SimpleCommand generateWrapperCommand(CommandExecutorWrapper<SimpleCommand, CommandSource> command) {
        return new WrapperCommand(command);
    }

    @Override
    public SimpleCommand generateRootCommand(String helpUsage, Predicate<CommandSource> extended) {
        return new RootCommand(helpUsage, extended);
    }

    @Override
    public SimpleCommand generateVersionCommand(String helpUsage, Predicate<CommandSource> extended) {
        return new VersionCommand(helpUsage, extended);
    }

    @Override
    public SimpleCommand generateHelpCommand(CommandNode<CommandSource> node) {
        return new HelpCommand(node);
    }

    @Override
    public SimpleCommand generateReloadCommand() {
        return new ReloadCommand();
    }
}
