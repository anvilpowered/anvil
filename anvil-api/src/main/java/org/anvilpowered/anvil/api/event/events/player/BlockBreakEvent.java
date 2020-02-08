package org.anvilpowered.anvil.api.event.events.player;

import org.anvilpowered.anvil.api.Interface.block.Block;
import org.anvilpowered.anvil.api.event.Cancellable;

public interface BlockBreakEvent extends PlayerEvent, Cancellable {

    Block getBlock();

}
