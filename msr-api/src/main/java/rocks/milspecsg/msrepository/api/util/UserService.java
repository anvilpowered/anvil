/*
 *     MSRepository - MilSpecSG
 *     Copyright (C) 2019 Cableguy20
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

package rocks.milspecsg.msrepository.api.util;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for translating UUIDs to UserNames or UserNames to UUIDs
 */
public interface UserService<TUser, TPlayer> {

    Optional<TUser> get(String userName);

    Optional<TUser> get(UUID userUUID);

    Optional<TPlayer> getPlayer(String userName);

    Optional<TPlayer> getPlayer(UUID userUUID);

    Optional<UUID> getUUID(String userName);

    Optional<String> getUserName(UUID userUUID);

    UUID getUUID(TUser user);

    String getUserName(TUser user);
}
