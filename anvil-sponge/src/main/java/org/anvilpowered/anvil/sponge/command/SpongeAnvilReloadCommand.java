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

import org.anvilpowered.anvil.common.command.CommonAnvilReloadCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SpongeAnvilReloadCommand
    extends CommonAnvilReloadCommand<Text, CommandSource>
    implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        Optional<String> optionalPlugin = context.getOne("plugin");
        String[] reloadedResult = {""};
        if (context.hasAny("a")) {
            reloadedResult[0] = doAll();
        } else if (!checkPresent(source, optionalPlugin.isPresent())) {
            return CommandResult.empty();
        } else if (context.hasAny("r")) {
            if (!doRegex(source, optionalPlugin.get(), reloadedResult)) {
                return CommandResult.empty();
            }
        } else if (!doDirect(source, optionalPlugin.get(), reloadedResult)) {
            return CommandResult.empty();
        }
        textService.builder()
            .append(pluginInfo.getPrefix())
            .green().append("Successfully reloaded ")
            .gold().append(reloadedResult[0])
            .sendTo(source);
        return CommandResult.success();
    }
}
