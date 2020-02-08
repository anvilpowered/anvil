package org.anvilpowered.anvil.api.event.events.entity;

import org.anvilpowered.anvil.api.Interface.entity.Entity;
import org.anvilpowered.anvil.api.event.Event;

public interface EntityEvent extends Event {

    Entity getEntity();

}
