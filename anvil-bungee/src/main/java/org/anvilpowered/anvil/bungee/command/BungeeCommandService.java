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

package org.anvilpowered.anvil.bungee.command;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.command.CommonCommandService;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BungeeCommandService extends CommonCommandService<Command,
    BiConsumer<CommandSender, String[]>, TextComponent, CommandSender> {

    private static class WrapperCommand implements BiConsumer<CommandSender, String[]>, TabExecutor {
        private final CommandExecutorWrapper<Command, CommandSender> wrapper;

        public WrapperCommand(CommandExecutorWrapper<Command, CommandSender> wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public void accept(CommandSender source, String[] context) {
            wrapper.execute(null, source, null, context);
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender source, String[] context) {
            return wrapper.suggest(null, source, null, context);
        }
    }

    private abstract static class BCommand implements BiConsumer<CommandSender, String[]> {

        protected final String helpUsage;
        protected final Predicate<CommandSender> extended;

        protected BCommand(String helpUsage, Predicate<CommandSender> extended) {
            this.helpUsage = helpUsage;
            this.extended = extended;
        }
    }

    private class RootCommand extends BCommand {
        public RootCommand(String helpUsage, Predicate<CommandSender> extended) {
            super(helpUsage, extended);
        }

        @Override
        public void accept(CommandSender source, String[] context) {
            sendRoot(source, helpUsage, extended.test(source));
        }
    }

    private class VersionCommand extends BCommand {

        public VersionCommand(String helpUsage, Predicate<CommandSender> extended) {
            super(helpUsage, extended);
        }

        @Override
        public void accept(CommandSender source, String[] context) {
            sendVersion(source, helpUsage, extended.test(source));
        }
    }

    private class HelpCommand implements BiConsumer<CommandSender, String[]> {

        private final CommandNode<CommandSender> node;

        private HelpCommand(CommandNode<CommandSender> node) {
            this.node = node;
        }

        @Override
        public void accept(CommandSender source, String[] context) {
            sendHelp(source, node);
        }
    }

    private class ReloadCommand implements BiConsumer<CommandSender, String[]> {

        @Override
        public void accept(CommandSender source, String[] context) {
            sendReload(source);
        }
    }

    @Override
    protected void runExecutor(
        BiConsumer<CommandSender, String[]> executor,
        Command command,
        CommandSender source,
        String alias,
        String[] context) {
        executor.accept(source, context);
    }

    @Override
    protected List<String> getSuggestions(
        BiConsumer<CommandSender, String[]> executor,
        Command command,
        CommandSender source,
        String alias,
        String[] context
    ) {
        if (executor instanceof TabExecutor) {
            Iterable<String> result = ((TabExecutor) executor).onTabComplete(source, context);
            if (result instanceof List) {
                return (List<String>) result;
            } else {
                return ImmutableList.copyOf(result);
            }
        }
        return ImmutableList.of();
    }

    @Override
    protected BiConsumer<CommandSender, String[]> generateWrapperCommand(CommandExecutorWrapper<Command, CommandSender> command) {
        return new WrapperCommand(command);
    }

    @Override
    public BiConsumer<CommandSender, String[]> generateRootCommand(String helpUsage, Predicate<CommandSender> extended) {
        return new RootCommand(helpUsage, extended);
    }

    @Override
    public BiConsumer<CommandSender, String[]> generateVersionCommand(String helpUsage, Predicate<CommandSender> extended) {
        return new VersionCommand(helpUsage, extended);
    }

    @Override
    public BiConsumer<CommandSender, String[]> generateHelpCommand(CommandNode<CommandSender> node) {
        return new HelpCommand(node);
    }

    @Override
    public BiConsumer<CommandSender, String[]> generateReloadCommand() {
        return new ReloadCommand();
    }
}
