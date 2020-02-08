package org.anvilpowered.anvil.api.Interface.world;

import org.anvilpowered.anvil.api.Interface.block.Block;
import org.anvilpowered.anvil.api.Interface.entity.Entity;

public interface World {

    public String getName();

    public Block getBlockAt(int x, int y, int z);

    public Block getBlockAt(Position position);

    public Entity spawnEntity(String name, Location location);

}
