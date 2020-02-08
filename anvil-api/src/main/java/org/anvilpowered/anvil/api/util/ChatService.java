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

package org.anvilpowered.anvil.api.util;

import org.anvilpowered.anvil.api.data.config.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ChatService<TString> {

    TString createChannel(String channelId, String prefix, List<String> aliases, Boolean alwaysVisible);

    //Return the prefix for a specified channel
    Optional<String> getPrefixForChannel(String channelId);

    //Return a channel for the specified channelId
    Optional<Channel> getChannelFromId(String channelId);

    //Change channels for a user
    TString switchChannel(UUID userUUID, String channelId);

    //return the current channel for a user
    String getChannelIdForUser(UUID userUUID);

    //Return a total count of users in a specified channel
    int getUserCountFromChannel(String channelId);

    //Return a list of users in a specified channel
    TString getUsersInChannel(String channelId);

    //Return a list of online players
    String getPlayerList();

    //Send a message to a specified channel
    CompletableFuture<Void> sendMessageToChannel(String channelId, TString message);

    //Send a message globally
    CompletableFuture<Void> sendGlobalMessage(TString message);
}
