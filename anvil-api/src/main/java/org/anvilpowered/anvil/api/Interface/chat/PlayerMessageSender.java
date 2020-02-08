package org.anvilpowered.anvil.api.Interface.chat;

import org.anvilpowered.anvil.api.Interface.entity.Player.Player;

public interface PlayerMessageSender extends MessageSender {

    Player getPlayer();

}
