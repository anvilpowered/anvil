package org.anvilpowered.anvil.api.event;

public interface Cancellable extends Event {

    void setCancelled(boolean cancelled);

    boolean isCancelled();

}
