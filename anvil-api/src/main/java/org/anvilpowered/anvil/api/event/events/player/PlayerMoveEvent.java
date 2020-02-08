package org.anvilpowered.anvil.api.event.events.player;

import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.api.event.Cancellable;

public interface PlayerMoveEvent extends PlayerEvent, Cancellable {

    Location getFrom();

    Location getTo();

    void setTo(Location to);

    void setFrom(Location from);

}
