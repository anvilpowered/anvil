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

package org.anvilpowered.anvil.sponge.util;

import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.common.util.CommonCommandService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
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
            List<Text> helpList = new ArrayList<>();
            String fullPath = getFullPath(node);
            node.getCommands().forEach((aliases, commandSpec) -> {
                if (!commandSpec.getShortDescription(source).isPresent()
                    || !commandSpec.testPermission(source)) return;
                String subCommand = aliases.toString().replace("[", "").replace("]", "");
                Text commandHelp = Text.builder()
                    .append(Text.builder()
                        .append(Text.of(TextColors.GREEN, fullPath, subCommand))
                        .build())
                    .append(Text.builder()
                        .append(Text.of(TextColors.GOLD, " - " + commandSpec.getShortDescription(source).get().toPlain() + "\n"))
                        .build())
                    .append(Text.builder()
                        .append(Text.of(TextColors.GRAY, "Usage: ", fullPath, subCommand, " ", commandSpec.getUsage(source).toPlain()))
                        .build())
                    .build();
                helpList.add(commandHelp);
            });
            helpList.sort(Text::compareTo);
            Sponge.getServiceManager().provide(PaginationService.class)
                .orElseThrow(() -> new CommandException(Text.of("Missing pagination service")))
                .builder()
                .title(Text.of(TextColors.GOLD, pluginInfo.getName(), " - ", pluginInfo.getOrganizationName()))
                .padding(Text.of(TextColors.DARK_GREEN, "-"))
                .contents(helpList)
                .linesPerPage(20)
                .build()
                .sendTo(source);
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
