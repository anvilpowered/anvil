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

import org.anvilpowered.anvil.api.misc.Named;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BackendServer extends Named {

    CompletableFuture<? extends Version> getVersion();
    
    CompletableFuture<Boolean> connect(@Nullable Object player);

    CompletableFuture<Boolean> connect(UUID userUUID);

    CompletableFuture<Boolean> connect(String userName);

    List<UUID> getPlayerUUIDs();
}
