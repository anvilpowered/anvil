package org.anvilpowered.anvil.api.event.events.player;

import org.anvilpowered.anvil.api.Interface.block.Action;
import org.anvilpowered.anvil.api.Interface.block.Block;
import org.anvilpowered.anvil.api.event.Cancellable;

public interface PlayerInteractEvent extends PlayerEvent, Cancellable {

    Action getAction();

    Block getBlock();

}
