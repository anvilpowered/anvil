package org.anvilpowered.anvil.api.Interface.entity.Player;

import org.anvilpowered.anvil.api.Interface.entity.Entity;
import org.anvilpowered.anvil.api.Interface.inventory.ArmoredInventory;
import org.anvilpowered.anvil.api.Interface.item.ItemStack;

public interface Player extends Entity {

    ArmoredInventory getInventory();

    ItemStack getHeldItemStack(Hand hand);

    String getUsername();

    void kick();

    void kick(String reason);

    void sendMessage(String message);

    boolean isAdmin();

    void setCanFly(boolean flying);

    boolean canFly();

    void setGamemode(Gamemode gamemode);

    Gamemode getGamemode();

    void playSound(String sound, float pitch, float volume);
}
