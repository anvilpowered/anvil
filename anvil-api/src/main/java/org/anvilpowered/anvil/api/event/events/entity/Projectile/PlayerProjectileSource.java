package org.anvilpowered.anvil.api.event.events.entity.Projectile;

import org.anvilpowered.anvil.api.Interface.entity.Player.Player;

public class PlayerProjectileSource {

    private Player player;

    public PlayerProjectileSource(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

}
