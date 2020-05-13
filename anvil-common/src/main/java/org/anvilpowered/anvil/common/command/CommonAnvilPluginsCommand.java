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
import org.anvilpowered.anvil.api.core.data.key.AnvilCoreKeys;
import org.anvilpowered.anvil.api.core.plugin.PluginMessages;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.PermissionService;
import org.anvilpowered.anvil.api.util.TextService;

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

    public void sendPlugins(TCommandSource source) {
        if (permissionService.hasPermission(source,
            registry.getOrDefault(AnvilCoreKeys.PLUGINS_PERMISSION))) {
            sendPluginsDirect(source);
        } else {
            textService.send(pluginMessages.getNoPermission(), source);
        }
    }

    public void sendPluginsDirect(TCommandSource source) {
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
