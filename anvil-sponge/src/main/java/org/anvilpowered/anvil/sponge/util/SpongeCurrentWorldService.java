/*
 *   Anvil - AnvilPowered
 *   Copyright (C) 2020
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

package org.anvilpowered.anvil.sponge.util;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.util.CurrentWorldService;
import org.anvilpowered.anvil.api.util.UserService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public class SpongeCurrentWorldService implements CurrentWorldService {

    @Inject
    protected UserService<User, Player> userService;

    protected Optional<String> getName(User user) {
        return user.getWorldUniqueId()
            .flatMap(u -> Sponge.getServer().getWorld(u))
            .map(World::getName);
    }

    @Override
    public Optional<String> getName(UUID userUUID) {
        return userService.get(userUUID).flatMap(this::getName);
    }

    @Override
    public Optional<String> getName(String userName) {
        return userService.get(userName).flatMap(this::getName);
    }
}
