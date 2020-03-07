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

package org.anvilpowered.anvil.common.command;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class CommonAnvilReloadCommand<TString, TCommandSource> {

    private static final Function<Environment, String> reloadEnvironment = e -> {
        e.reload();
        return e.getPluginInfo().getName();
    };

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    protected String doAll() {
        return Anvil.getEnvironmentManager()
            .getEnvironments().values().stream()
            .map(reloadEnvironment)
            .collect(Collectors.joining(", "));
    }

    @Contract("_, _ -> param2")
    protected boolean checkPresent(TCommandSource source, boolean present) {
        if (present) {
            return true;
        }
        textService.builder()
            .append(pluginInfo.getPrefix())
            .red().append("Plugin is required if '--all' is not set")
            .sendTo(source);
        return false;
    }

    protected boolean doRegex(TCommandSource source, String regex, String[] reloadedResult) {
        try {
            reloadedResult[0] = Anvil.getEnvironmentManager()
                .getEnvironmentsAsStream(Pattern.compile(regex))
                .map(reloadEnvironment)
                .collect(Collectors.joining(", "));
            if (reloadedResult[0].length() == 0) {
                textService.builder()
                    .append(pluginInfo.getPrefix())
                    .red().append("Regex ")
                    .gold().append(regex)
                    .red().append(" did not match any plugins")
                    .sendTo(source);
                return false;
            }
        } catch (PatternSyntaxException e) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append("Failed to parse ")
                .gold().append(regex)
                .sendTo(source);
            return false;
        }
        return true;
    }

    protected boolean doDirect(TCommandSource source, String plugin, String[] reloadedResult) {
        Optional<String> optionalReloaded = Anvil.getEnvironmentManager()
            .getEnvironment(plugin)
            .map(reloadEnvironment);
        if (!optionalReloaded.isPresent()) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append("Could not find plugin ")
                .gold().append(plugin)
                .sendTo(source);
            return false;
        }
        reloadedResult[0] = optionalReloaded.get();
        return true;
    }
}
