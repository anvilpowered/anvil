package org.anvilpowered.anvil.api.event.events.player;

import org.anvilpowered.anvil.api.Interface.entity.Player.Player;
import org.anvilpowered.anvil.api.event.Event;

public interface PlayerEvent extends Event {

    Player getPlayer();

}
