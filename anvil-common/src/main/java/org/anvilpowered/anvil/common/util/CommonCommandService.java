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

package org.anvilpowered.anvil.common.util;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.command.CommandService;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonCommandService<
    TCommand,
    TCommandExecutor,
    TString,
    TCommandSource>
    implements CommandService<TCommand, TCommandExecutor, TCommandSource> {

    @Inject
    protected Environment environment;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    protected void sendRoot(TCommandSource source, String helpCommandName, boolean extended) {
        textService.builder()
            .append(pluginInfo.getPrefix())
            .aqua().append("Running version ")
            .green().append(pluginInfo.getVersion())
            .aqua().append(" by ")
            .appendJoining(", ", pluginInfo.getAuthors())
            .append("\n")
            .appendIf(extended, textService.builder()
                .green().append("Use ")
                .gold().append(helpCommandName)
                .green().append(" for help")
            )
            .appendIf(!extended, textService.builder()
                .red().append("You do not have permission for any sub-commands")
            )
            .sendTo(source);
    }

    protected void sendVersion(TCommandSource source, String helpCommandName, boolean extended) {
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
                .gold().append(helpCommandName)
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

    protected String getFullPath(CommandNode<TCommand> node) {
        StringBuilder s = new StringBuilder(32);
        s.append("/");
        for (String name : node.getPath()) {
            s.append(name).append(" ");
        }
        s.append(node.getName()).append(" ");
        return s.toString();
    }

    protected void sendHelp(TCommandSource source, CommandNode<TCommand> node) {
        List<TString> helpList = new ArrayList<>();
        String fullPath = getFullPath(node);

        node.getCommands().forEach((aliases, command) -> {

            if (!node.getDescriptions().containsKey(aliases)
                || !node.getPermissions().get(aliases).test(source)) return;

            String subCommand = aliases.toString().replace("[", "").replace("]", "");
            helpList.add(textService.builder()
                .gold().append(fullPath, subCommand)
                .gold().append(" - " + node.getDescriptions().get(aliases).apply(source) + "\n")
                .gray().append("Usage: ", fullPath)
                .appendJoining(", ", aliases.toArray())
                .append(" ", node.getUsages().get(aliases).apply(source))
                .build());
        });
        textService.paginationBuilder()
            .title(textService.builder().gold().append(pluginInfo.getName(), " - ", pluginInfo.getOrganizationName()).build())
            .padding(textService.builder().dark_green().append("-").build())
            .contents(helpList)
            .build()
            .sendTo(source);
    }
}
