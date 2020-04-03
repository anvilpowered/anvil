package org.anvilpowered.anvil.bungee.util;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.anvilpowered.anvil.api.util.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class BungeeUserService implements UserService<ProxiedPlayer, ProxiedPlayer> {

    @Override
    public Optional<ProxiedPlayer> get(String userName) {
        return Optional.of(ProxyServer.getInstance().getPlayer(userName));
    }

    @Override
    public Optional<ProxiedPlayer> get(UUID userUUID) {
        return Optional.of(ProxyServer.getInstance().getPlayer(userUUID));
    }

    @Override
    public Optional<ProxiedPlayer> getPlayer(String userName) {
        return get(userName);
    }

    @Override
    public Optional<ProxiedPlayer> getPlayer(UUID userUUID) {
        return get(userUUID);
    }

    @Override
    public Collection<ProxiedPlayer> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers();
    }

    @Override
    public Optional<UUID> getUUID(String userName) {
        return getPlayer(userName).map(ProxiedPlayer::getUniqueId);
    }

    @Override
    public Optional<String> getUserName(UUID userUUID) {
        return Optional.empty();
    }

    @Override
    public UUID getUUID(ProxiedPlayer proxiedPlayer) {
        return proxiedPlayer.getUniqueId();
    }

    @Override
    public String getUserName(ProxiedPlayer proxiedPlayer) {
        return proxiedPlayer.getDisplayName();
    }
}
