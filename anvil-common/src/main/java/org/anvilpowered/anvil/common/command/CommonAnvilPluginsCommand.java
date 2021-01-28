/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020-2021
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
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginMessages;
import org.anvilpowered.anvil.api.registry.Keys;
import org.anvilpowered.anvil.api.registry.Registry;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;
import org.jetbrains.annotations.Nullable;

public class CommonAnvilPluginsCommand<TString, TCommandSource> {

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

    public void execute(TCommandSource source) {
        execute(source, null);
    }

    public void execute(TCommandSource source, String @Nullable [] context) {
        if (permissionService.hasPermission(source,
            registry.getOrDefault(Keys.PLUGINS_PERMISSION))) {
            executeDirect(source);
        } else {
            textService.send(pluginMessages.getNoPermission(), source);
        }
    }

    public void executeDirect(TCommandSource source) {
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
    }
}
