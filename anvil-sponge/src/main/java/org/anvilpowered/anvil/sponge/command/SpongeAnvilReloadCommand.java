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
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class SpongeAnvilReloadCommand implements CommandExecutor {

    private static final Function<Environment, String> reloadEnvironment = e -> {
        e.reload();
        return e.getPluginInfo().getName();
    };

    @Inject
    private PluginInfo<Text> pluginInfo;

    @Inject
    private TextService<Text, CommandSource> textService;

    @Override
    public CommandResult execute(CommandSource source, CommandContext context) {
        Optional<String> optionalPlugin = context.getOne("plugin");
        String reloadedResult;
        if (context.hasAny("a")) {
            reloadedResult = Anvil.getEnvironmentManager()
                .getEnvironments().values().stream()
                .map(reloadEnvironment)
                .collect(Collectors.joining(", "));
        } else if (!optionalPlugin.isPresent()) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append("Plugin is required if '--all' is not set")
                .sendTo(source);
            return CommandResult.empty();
        } else if (context.hasAny("r")) {
            String regex = optionalPlugin.get();
            try {
                reloadedResult = Anvil.getEnvironmentManager()
                    .getEnvironmentsAsStream(Pattern.compile(regex))
                    .map(reloadEnvironment)
                    .collect(Collectors.joining(", "));
                if (reloadedResult.length() == 0) {
                    textService.builder()
                        .append(pluginInfo.getPrefix())
                        .red().append("Regex ")
                        .gold().append(regex)
                        .red().append(" did not match any plugins")
                        .sendTo(source);
                    return CommandResult.empty();
                }
            } catch (PatternSyntaxException e) {
                textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Failed to parse ")
                    .gold().append(regex)
                    .sendTo(source);
                return CommandResult.empty();
            }
        } else {
            String plugin = optionalPlugin.get();
            Optional<String> optionalReloaded = Anvil.getEnvironmentManager()
                .getEnvironment(optionalPlugin.get())
                .map(reloadEnvironment);
            if (!optionalReloaded.isPresent()) {
                textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Could not find plugin ")
                    .gold().append(plugin)
                    .sendTo(source);
                return CommandResult.empty();
            }
            reloadedResult = optionalReloaded.get();
        }
        textService.builder()
            .append(pluginInfo.getPrefix())
            .green().append("Successfully reloaded ")
            .gold().append(reloadedResult)
            .sendTo(source);
        return CommandResult.success();
    }
}
