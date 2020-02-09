package org.anvilpowered.anvil.core.spigot.implementation.entity;

import org.anvilpowered.anvil.api.Interface.entity.Entity;
import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.api.util.Vector;

public class SpigotEntity implements Entity {

    private org.bukkit.entity.Entity spigotEntity;

    public SpigotEntity(org.bukkit.entity.Entity spigotEntity) {
        this.spigotEntity = spigotEntity;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(Location l) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(Vector velocity) {

    }

    @Override
    public void addVelocity(Vector velocity) {

    }
}
