package org.anvilpowered.anvil.api.util;

import org.anvilpowered.anvil.api.Interface.entity.Player.Player;
import org.anvilpowered.anvil.api.Interface.world.Location;

public interface SoundService {

    void playSound(String sound, float pitch, float volume, Location location);

    void playSound(String sound, float pitch, float volume, Player player);

}
