package org.anvilpowered.anvil.spigot.util;

import org.anvilpowered.anvil.api.util.TeleportationService;
import org.anvilpowered.anvil.api.util.UserService;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

public class SpigotTeleportationService implements TeleportationService {

    @Inject
    protected UserService<Player, Player> userService;

    @Override
    public boolean teleport(UUID teleportingUserUUID, UUID targetUserUUID) {
        final Optional<Player> teleporter = userService.get(teleportingUserUUID);
        final Optional<Player> target = userService.get(targetUserUUID);

        if(!teleporter.isPresent() || !target.isPresent()) {
            return false;
        }
        return teleporter.get().teleport(target.get().getLocation());
    }
}
