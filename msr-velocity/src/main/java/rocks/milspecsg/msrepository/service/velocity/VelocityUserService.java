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

package rocks.milspecsg.msrepository.service.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.UuidUtils;
import rocks.milspecsg.msrepository.api.UserService;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class VelocityUserService implements UserService<Player> {

    @Inject
    private ProxyServer proxyServer;

    @Override
    public Optional<Player> get(String userName) {
        return proxyServer.getPlayer(userName);
    }

    @Override
    public Optional<Player> get(UUID userUUID) {
        return proxyServer.getPlayer(userUUID);
    }

    @Override
    public Optional<UUID> getUUID(String userName) {
        return Optional.of(UuidUtils.generateOfflinePlayerUuid(userName));
    }

    @Override
    public Optional<String> getUserName(UUID userUUID) {
        return get(userUUID).map(Player::getUsername);
    }

    @Override
    public UUID getUUID(Player player) {
        return player.getUniqueId();
    }

    @Override
    public String getUserName(Player player) {
        return player.getUsername();
    }
}
