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

package org.anvilpowered.anvil.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CommonCommandService<
    TCommand,
    TCommandExecutor,
    TString,
    TCommandSource>
    implements CommandService<TCommandExecutor, TCommandSource> {

    @Inject
    protected Environment environment;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    protected abstract void runExecutor(
        TCommandExecutor executor,
        TCommand command,
        TCommandSource source,
        String alias,
        String[] context
    );

    protected abstract TCommandExecutor generateWrapperCommand(
        CommandExecutorWrapper<TCommand, TCommandSource> command);

    protected interface CommandExecutorWrapper<TCommand, TCommandSource> {
        void execute(TCommand command, TCommandSource source, String alias, String[] context);
    }

    private void sendSubCommandError(
        TCommandSource source,
        @Nullable String command,
        Map<List<String>, TCommandExecutor> children
    ) {
        textService.builder()
            .red().append("Input command ", command == null ? "null" : command)
            .append(" was not a valid subcommand!\n")
            .appendIf(command != null, command, "\n^")
            .append("\nAvailable: ")
            .append(children.keySet().stream()
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .collect(Collectors.joining(", "))
            )
            .sendTo(source);
    }

    @Override
    public TCommandExecutor generateRoutingCommand(
        @Nullable TCommandExecutor root,
        Map<List<String>, TCommandExecutor> children,
        boolean childCommandFallback
    ) {
        return generateWrapperCommand((command, source, alias, context) -> {
            final int length = context.length;
            if (length > 0) {
                for (Map.Entry<List<String>, TCommandExecutor> entry : children.entrySet()) {
                    final List<String> key = entry.getKey();
                    if (key.contains(context[0])) {
                        runExecutor(entry.getValue(), command, source, context[0],
                            // remove the first argument (the child selector)
                            Arrays.copyOfRange(context, 1, length));
                        return;
                    }
                }
                if (!childCommandFallback) {
                    sendSubCommandError(source, context[0], children);
                    return;
                }
            }
            // reached the end; if no children have
            // matched, run the root command
            if (root == null) {
                sendSubCommandError(source, null, children);
                return;
            }
            runExecutor(root, command, source, alias, context);
        });
    }

    protected void sendRoot(TCommandSource source, String helpUsage, boolean extended) {
        textService.builder()
            .append(pluginInfo.getPrefix())
            .aqua().append("Running version ")
            .green().append(pluginInfo.getVersion())
            .aqua().append(" by ")
            .appendJoining(", ", pluginInfo.getAuthors())
            .append("\n")
            .appendIf(extended, textService.builder()
                .green().append("Use ")
                .gold().append(helpUsage)
                .green().append(" for help")
            )
            .appendIf(!extended, textService.builder()
                .red().append("You do not have permission for any sub-commands")
            )
            .sendTo(source);
    }

    protected void sendVersion(TCommandSource source, String helpUsage, boolean extended) {
        textService.builder()
            .append(pluginInfo.getPrefix())
            .aqua().append("Running version ")
            .green().append(pluginInfo.getVersion())
            .aqua().append(" by ")
            .appendJoining(", ", pluginInfo.getAuthors())
            .gray().append("\nBuild date: ")
            .aqua().append(pluginInfo.getBuildDate(), "\n")
            .appendIf(extended, textService.builder()
                .green().append("Use ")
                .gold().append(helpUsage)
                .green().append(" for help")
            )
            .appendIf(!extended, textService.builder()
                .red().append("You do not have permission for any sub-commands")
            )
            .sendTo(source);
    }

    protected void sendReload(TCommandSource source) {
        environment.reload();
        textService.builder()
            .append(pluginInfo.getPrefix())
            .green().append("Successfully reloaded!")
            .sendTo(source);
    }

    protected String getFullPath(CommandNode<TCommandSource> node) {
        StringBuilder s = new StringBuilder(32);
        s.append("/");
        for (String name : node.getPath()) {
            s.append(name).append(" ");
        }
        s.append(node.getName()).append(" ");
        return s.toString();
    }

    protected void sendHelp(TCommandSource source, CommandNode<TCommandSource> node) {
        List<TString> helpList = new ArrayList<>();
        String fullPath = getFullPath(node);

        node.getDescriptions().forEach((aliases, command) -> {
            Predicate<TCommandSource> permission = node.getPermissions().get(aliases);
            if (permission != null && permission.test(source)) return;

            Function<TCommandSource, String> description = node.getDescriptions().get(aliases);
            Function<TCommandSource, String> usage = node.getUsages().get(aliases);

            TextService.Builder<TString, TCommandSource> builder = textService.builder()
                .gold().append(fullPath).appendIf(aliases.size() > 0, aliases.get(0));
            if (description != null) {
                builder.append(" - ", description.apply(source));
            }
            builder.gray().append("\nUsage: ", fullPath)
                .appendJoining(", ", aliases.toArray());
            if (usage != null) {
                builder.append(" ", usage.apply(source));
            }

            helpList.add(builder.build());
        });
        textService.paginationBuilder()
            .title(textService.builder().gold().append(pluginInfo.getName(), " - ", pluginInfo.getOrganizationName()).build())
            .padding(textService.builder().dark_green().append("-").build())
            .contents(helpList)
            .build()
            .sendTo(source);
    }
}
