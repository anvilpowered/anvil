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

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class SpongeAnvilPluginsCommand implements CommandExecutor {

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Inject
    private TextService<Text, CommandSource> textService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        String[] names = Anvil.getEnvironmentManager()
            .getEnvironments().values()
            .stream()
            .map(e -> e.getPluginInfo().getName())
            .sorted().toArray(String[]::new);
        textService.builder()
            .append(pluginInfo.getPrefix())
            .green().append("Plugins (", names.length, "): ")
            .appendJoining(", ", names)
            .sendTo(source);
        return CommandResult.success();
    }
}
