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

package org.anvilpowered.anvil.api.server;

import org.spongepowered.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface LocationService {

    Optional<? extends BackendServer> getServer(UUID userUUID);

    Optional<? extends BackendServer> getServer(String userName);

    Optional<? extends BackendServer> getServerForName(String serverName);

    List<? extends BackendServer> getServers();

    Optional<String> getWorldName(UUID userUUID);

    Optional<String> getWorldName(String userName);

    Optional<Vector3d> getPosition(UUID userUUID);

    Optional<Vector3d> getPosition(String userName);

    CompletableFuture<Boolean> teleport(UUID teleportingUserUUID, UUID targetUserUUID);
}
