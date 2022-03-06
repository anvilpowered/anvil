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

package org.anvilpowered.anvil.common.plugin;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.plugin.PluginInfo;
import org.anvilpowered.anvil.api.plugin.PluginMessages;
import org.anvilpowered.anvil.api.util.TextService;
import org.anvilpowered.anvil.api.util.TimeFormatService;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class AnvilPluginMessages<TString, TCommandSource> implements PluginMessages<TString> {

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected TextService<TString, TCommandSource> textService;

    @Inject
    protected TimeFormatService timeFormatService;

    @Override
    public TString getBanMessage(String reason, Instant endUtc) {
        return textService.builder()
            .red().append("You have been banned for: ", textService.deserialize(reason))
            .yellow().append("\n\nFor another ", timeFormatService.format(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(), endUtc)))
            .append("\n\nUntil ", timeFormatService.format(endUtc))
            .build();
    }

    @Override
    public TString getMuteMessage(String reason, Instant endUtc) {
        return textService.builder()
            .append(pluginInfo.getPrefix())
            .red().append("You have been muted for: ", textService.deserialize(reason))
            .yellow().append("\nFor another ", timeFormatService.format(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(), endUtc)))
            .build();
    }

    @Override
    public TString getNoPermission() {
        return textService.builder()
            .append(pluginInfo.getPrefix())
            .red().append("You do not have permission for this command!")
            .build();
    }
}
