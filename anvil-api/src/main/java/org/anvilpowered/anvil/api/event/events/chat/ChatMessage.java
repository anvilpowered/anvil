package org.anvilpowered.anvil.api.event.events.chat;

import org.anvilpowered.anvil.api.Interface.chat.MessageSender;
import org.anvilpowered.anvil.api.event.Cancellable;
import org.anvilpowered.anvil.api.event.Event;

public interface ChatMessage extends Event, Cancellable {

    String getMessage();

    void setMessage(String message);

    MessageSender getSender();

}
