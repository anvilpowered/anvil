package org.anvilpowered.anvil.api.Interface.entity;

import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.api.util.Vector;

public interface Entity {

    Location getLocation();

    void setLocation(Location l);

    String getId();

    Vector getVelocity();

    void setVelocity(Vector velocity);

    void addVelocity(Vector velocity);

}
