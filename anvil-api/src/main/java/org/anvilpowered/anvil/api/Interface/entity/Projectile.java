package org.anvilpowered.anvil.api.Interface.entity;

import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.api.util.Vector;

public interface Projectile extends Entity {

    Vector getVelocity();

    void setVelocity(Vector velocity);

    Location getLocation();

}
