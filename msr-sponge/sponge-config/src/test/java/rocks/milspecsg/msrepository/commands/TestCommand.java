/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.commands;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ConfigKeys;

import java.util.List;

public class TestCommand implements CommandExecutor {

    @Inject
    ConfigurationService configurationService;


    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        source.sendMessage(Text.of("test start"));

        configurationService.addToConfigList(ConfigKeys.SOME_LIST, 15, new TypeToken<List<Integer>>() {});

        configurationService.save();

        return CommandResult.success();
    }
}
