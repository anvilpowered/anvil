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

package org.anvilpowered.anvil.common.util;

import org.anvilpowered.anvil.api.data.config.Channel;
import org.anvilpowered.anvil.api.data.key.Keys;
import org.anvilpowered.anvil.api.data.registry.Registry;
import org.anvilpowered.anvil.api.util.ChatService;
import org.anvilpowered.anvil.api.util.StringResult;
import org.anvilpowered.anvil.api.util.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class CommonChatService<TPlayer extends TCommandSource, TString, TCommandSource> implements ChatService<TString> {

    @Inject
    protected StringResult<TString, TCommandSource> stringResult;

    @Inject
    protected Registry registry;

    @Inject
    protected UserService<TPlayer, TPlayer> userService;

    Map<UUID, String> channelMap = new HashMap<>();

    @Override
    public TString createChannel(String channelId, String prefix, List<String> aliases, Boolean alwaysVisible) {
        Channel channel = new Channel();
        channel.id = channelId;
        channel.prefix = prefix;
        channel.aliases = aliases;
        channel.alwaysVisible = alwaysVisible;

        return stringResult.success("Successfully created channel " + channelId);
    }

    @Override
    public Optional<String> getPrefixForChannel(String channelId) {
        return getChannelFromId(channelId).map(c -> c.prefix);
    }

    @Override
    public Optional<Channel> getChannelFromId(String channelId) {
        return registry.get(Keys.<List<Channel>>resolveUnsafe("CHANNEL_ID")).flatMap((List<Channel> channel) ->
            channel.stream()
                .filter(c -> c.id.equals(channelId))
                .findAny());
    }

    @Override
    public TString switchChannel(UUID userUUID, String channelId) {
        if (getChannelFromId(channelId).isPresent()) {
            channelMap.put(userUUID, channelId);
            return stringResult.success("Connected to channel " + channelId);
        }
        return stringResult.fail("Failed to connect to channel " + channelId);
    }

    @Override
    public String getChannelIdForUser(UUID userUUID) {
        return channelMap.get(userUUID) == null ? registry.getOrDefault(Keys.resolveUnsafe("CHANNEL_DEFAULT")) : channelMap.get(userUUID);
    }

    @Override
    public int getUserCountFromChannel(String channelId) {
        return (int) userService.getOnlinePlayers()
            .stream()
            .filter(p -> getChannelIdForUser(userService.getUUID(p))
                .equals(channelId)).count();
    }

    @Override
    public TString getUsersInChannel(String channelId) {
        List<String> channelUsersList = userService.getOnlinePlayers()
            .stream()
            .filter(p -> getChannelIdForUser(userService.getUUID(p)).equals(channelId))
            .map(p -> userService.getUserName(p))
            .collect(Collectors.toList());

        return stringResult.builder()
            .green().append("------------------- ")
            .gold().append(channelId)
            .green().append(" --------------------\n")
            .append(String.join(", ", channelUsersList))
            .build();
    }

    @Override
    public String getPlayerList() {
        return userService.getOnlinePlayers().stream().map(userService::getUserName).collect(Collectors.joining(", \n"));
    }

    @Override
    public CompletableFuture<Void> sendMessageToChannel(String channelId, TString message) {
        return CompletableFuture.runAsync(() -> userService.getOnlinePlayers().forEach(p -> {
            if (getChannelIdForUser(userService.getUUID(p)).equals(channelId))
                stringResult.send(message, p);
        }));
    }

    @Override
    public CompletableFuture<Void> sendGlobalMessage(TString message) {
        return CompletableFuture.runAsync(() -> userService.getOnlinePlayers().forEach(p -> stringResult.send(message, p)));
    }
}
