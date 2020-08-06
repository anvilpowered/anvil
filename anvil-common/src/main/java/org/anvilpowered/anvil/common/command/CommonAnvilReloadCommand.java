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
import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Environment;
import org.anvilpowered.anvil.api.misc.Named;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginMessages;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.jetbrains.annotations.Contract;

import java.util.List;
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
    protected PermissionService permissionService;

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected PluginMessages<TString> pluginMessages;

    @Inject
    protected Registry registry;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    public void execute(TCommandSource source, String[] context) {
        if (permissionService.hasPermission(source,
            registry.getOrDefault(Keys.RELOAD_PERMISSION))) {
            executeDirect(source, context);
        } else {
            textService.send(pluginMessages.getNoPermission(), source);
        }
    }

    public List<String> suggest() {
        List<String> suggestions = Anvil.getEnvironmentManager()
            .getEnvironments().values().stream()
            .map(Named::getName)
            .sorted().collect(Collectors.toList());
        suggestions.add("--all");
        suggestions.add("--regex");
        return suggestions;
    }

    public List<String> suggest(TCommandSource source, String[] context) {
        return suggest();
    }

    public void executeDirect(TCommandSource source, String[] context) {
        final int length = context.length;
        if (length == 0) {
            checkPresent(source, false);
            return;
        }
        String[] reloadedResult = {""};
        if ("-a".equals(context[0]) || "--all".equals(context[0])) {
            reloadedResult[0] = doAll();
        } else if ("-r".equals(context[0]) || "--regex".equals(context[0])) {
            if (!checkPresent(source, length > 1)
                || !doRegex(source, context[1], reloadedResult)) {
                return;
            }
        } else {
            if (!doDirect(source, context[0], reloadedResult)) {
                return;
            }
        }
        sendSuccess(source, reloadedResult);
    }

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

    protected void sendSuccess(TCommandSource source, String[] reloadedResult) {
        textService.builder()
            .append(pluginInfo.getPrefix())
            .green().append("Successfully reloaded ")
            .gold().append(reloadedResult[0])
            .sendTo(source);
    }
}
