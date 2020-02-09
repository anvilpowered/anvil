package org.anvilpowered.anvil.core.spigot.implementation.world;

import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Interface.block.Block;
import org.anvilpowered.anvil.api.Interface.entity.Entity;
import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.api.Interface.world.Position;
import org.anvilpowered.anvil.api.Interface.world.World;

public class SpigotWorld implements World {

    private org.bukkit.World spigotWorld;

    public SpigotWorld(org.bukkit.World world) {
        this.spigotWorld = world;
    }

    @Override
    public String getName() {
        return spigotWorld.getName();
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return Anvil.getAnvilInstane;
    }

    @Override
    public Block getBlockAt(Position position) {
        return null;
    }

    @Override
    public Entity spawnEntity(String name, Location location) {
        return null;
    }
}
