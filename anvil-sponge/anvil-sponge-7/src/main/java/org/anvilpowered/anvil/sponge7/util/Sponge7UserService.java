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

package org.anvilpowered.anvil.sponge7.util;

import com.google.inject.Inject;
import org.anvilpowered.anvil.common.util.CommonUserService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Sponge7UserService extends CommonUserService<User, Player> {

    @Inject
    public Sponge7UserService() {
        super(User.class);
    }

    @Override
    public Optional<User> get(String userName) {
        return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(u -> u.get(userName));
    }

    @Override
    public Optional<User> get(UUID userUUID) {
        return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(u -> u.get(userUUID));
    }

    @Override
    public Optional<Player> getPlayer(String userName) {
        return get(userName).flatMap(User::getPlayer);
    }

    @Override
    public Optional<Player> getPlayer(UUID userUUID) {
        return get(userUUID).flatMap(User::getPlayer);
    }

    @Override
    public Optional<Player> getPlayer(User user) {
        return user.getPlayer();
    }

    @Override
    public List<String> matchPlayerNames(String lastKnownName) {
        return Sponge.getServiceManager().provideUnchecked(UserStorageService.class).match(lastKnownName).stream()
            .map(GameProfile::getName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers();
    }

    @Override
    public CompletableFuture<Optional<UUID>> getUUID(String userName) {
        Optional<UUID> userUUID = getPlayer(userName).map(Player::getUniqueId);
        if (userUUID.isPresent()) {
            return CompletableFuture.completedFuture(userUUID);
        }
        return super.getUUID(userName);
    }

    @Override
    public CompletableFuture<Optional<String>> getUserName(UUID userUUID) {
        Optional<String> userName = getPlayer(userUUID).map(Player::getName);
        if (userName.isPresent()) {
            return CompletableFuture.completedFuture(userName);
        }
        return super.getUserName(userUUID);
    }

    @Override
    public UUID getUUID(User user) {
        return user.getUniqueId();
    }

    @Override
    public String getUserName(User user) {
        return user.getName();
    }
}
