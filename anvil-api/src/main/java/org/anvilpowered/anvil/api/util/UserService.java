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

package org.anvilpowered.anvil.api.util;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for translating UUIDs to UserNames or UserNames to UUIDs
 */
public interface UserService<TUser, TPlayer> {

    Optional<TUser> get(String userName);

    Optional<TUser> get(UUID userUUID);

    Optional<TPlayer> getPlayer(String userName);

    Optional<TPlayer> getPlayer(UUID userUUID);

    Optional<TPlayer> getPlayer(TUser user);

    Collection<TPlayer> getOnlinePlayers();

    CompletableFuture<Optional<UUID>> getUUID(String userName);

    CompletableFuture<Optional<String>> getUserName(UUID userUUID);

    UUID getUUID(TUser user);

    /**
     * If the provided object has a {@link UUID}, return it. Otherwise return a constant UUID that is
     * the same for all objects without a UUID.
     */
    UUID getUUIDSafe(Object object);

    String getUserName(TUser user);
}
