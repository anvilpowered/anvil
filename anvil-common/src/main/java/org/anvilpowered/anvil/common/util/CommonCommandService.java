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
import org.anvilpowered.anvil.api.command.CommandNode;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.CommandService;
import org.anvilpowered.anvil.api.util.TextService;

public abstract class CommonCommandService<
    TCommand,
    TCommandExecutor,
    TString,
    TCommandSource>
    implements CommandService<TCommand, TCommandExecutor, TCommandSource> {

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    protected void sendRoot(TCommandSource source, String helpCommandName, boolean extended) {
        textService.builder()
            .append().append(pluginInfo.getPrefix())
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
            .append().append(pluginInfo.getPrefix())
            .aqua().append("Running version ", pluginInfo.getVersion(), " by ",
            String.join(", ", pluginInfo.getAuthors()))
            .appendIf(extended, textService.builder()
                .green().append("Use ")
                .gold().append(helpCommandName)
                .green().append(" for help")
            )
            .appendIf(!extended, textService.builder()
                .red().append("You do not have permission for any sub-commands")
            )
            .append("Build date")
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
}
