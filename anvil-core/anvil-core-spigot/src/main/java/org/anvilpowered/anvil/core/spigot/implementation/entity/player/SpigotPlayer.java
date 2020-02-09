package org.anvilpowered.anvil.core.spigot.implementation.entity.player;

import org.anvilpowered.anvil.api.Interface.entity.Player.Gamemode;
import org.anvilpowered.anvil.api.Interface.entity.Player.Hand;
import org.anvilpowered.anvil.api.Interface.entity.Player.Player;
import org.anvilpowered.anvil.api.Interface.inventory.ArmoredInventory;
import org.anvilpowered.anvil.api.Interface.item.ItemStack;
import org.anvilpowered.anvil.core.spigot.implementation.entity.SpigotEntity;
import org.bukkit.entity.Entity;

public class SpigotPlayer extends SpigotEntity implements Player {

    private org.bukkit.entity.Player spigotPlayer;

    public SpigotPlayer(org.bukkit.entity.Player player) {
        super(player);
        this.spigotPlayer = player;
    }

    @Override
    public ArmoredInventory getInventory() {
        return null;
    }

    @Override
    public ItemStack getHeldItemStack(Hand hand) {
        return null;
    }

    @Override
    public String getUsername() {
        return spigotPlayer.getPlayerListName();
    }

    @Override
    public void kick() {
        kick("No reason provided.");
    }

    @Override
    public void kick(String reason) {
        spigotPlayer.kickPlayer(reason);
    }

    @Override
    public void sendMessage(String message) {
        spigotPlayer.sendMessage(message);
    }

    @Override
    public boolean isAdmin() {
        return spigotPlayer.isOp();
    }

    @Override
    public void setCanFly(boolean flying) {
        spigotPlayer.setFlying(flying);
    }

    @Override
    public boolean canFly() {
        return spigotPlayer.isFlying();
    }

    @Override
    public void setGamemode(Gamemode gamemode) {
    }

    @Override
    public Gamemode getGamemode() {
        return null;
    }

    @Override
    public void playSound(String sound, float pitch, float volume) {

    }
}
