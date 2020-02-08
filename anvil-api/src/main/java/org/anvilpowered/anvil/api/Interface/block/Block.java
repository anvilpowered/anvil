package org.anvilpowered.anvil.api.Interface.block;

import org.anvilpowered.anvil.api.Interface.world.Location;

public interface Block {

    String getName();

    String getKey();

    Location getLocation();

    void setType(String key);

}
