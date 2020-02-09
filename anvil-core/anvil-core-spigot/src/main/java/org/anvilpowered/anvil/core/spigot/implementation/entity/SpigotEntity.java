package org.anvilpowered.anvil.core.spigot.implementation.entity;

import org.anvilpowered.anvil.api.Interface.entity.Entity;
import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.api.util.Vector;
import org.anvilpowered.anvil.core.spigot.helper.SpigotLocationHelper;

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
        spigotEntity.teleport(SpigotLocationHelper.fromAnvil(l));
    }

    @Override
    public String getId() {
        return spigotEntity.getType().getKey().getKey();
    }

    @Override
    public Vector getVelocity() {
        return new Vector(spigotEntity.getVelocity().getX(), spigotEntity.getVelocity().getY(), spigotEntity.getVelocity().getZ());
    }

    @Override
    public void setVelocity(Vector velocity) {
        spigotEntity.setVelocity(new org.bukkit.util.Vector(velocity.getX(), velocity.getY(), velocity.getZ()));
    }

    @Override
    public void addVelocity(Vector velocity) {
        spigotEntity.setVelocity(new org.bukkit.util.Vector(velocity.getX(), velocity.getY(), velocity.getZ()).add(spigotEntity.getVelocity()));
    }
}
