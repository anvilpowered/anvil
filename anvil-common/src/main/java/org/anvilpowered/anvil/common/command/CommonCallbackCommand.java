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
import com.google.inject.Singleton;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.util.TextService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Singleton
public class CommonCallbackCommand<TString, TCommandSource> {

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    private final Map<UUID, Consumer<TCommandSource>> callbacks;

    public CommonCallbackCommand() {
        callbacks = new HashMap<>();
    }

    public void addCallback(UUID uuid, Consumer<TCommandSource> callback) {
        callbacks.put(uuid, callback);
    }

    public void execute(TCommandSource source, String[] context) {
        if (context.length == 0) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append("Missing callback id")
                .sendTo(source);
            return;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(context[0]);
        } catch (IllegalArgumentException e) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append("Callback id must be a valid UUID")
                .sendTo(source);
            return;
        }
        Consumer<TCommandSource> callback = callbacks.get(uuid);
        if (callback == null) {
            textService.builder()
                .append(pluginInfo.getPrefix())
                .red().append("Callback id ")
                .gold().append(uuid)
                .red().append(" does not match any callbacks")
                .sendTo(source);
            return;
        }
        callback.accept(source);
    }
}
