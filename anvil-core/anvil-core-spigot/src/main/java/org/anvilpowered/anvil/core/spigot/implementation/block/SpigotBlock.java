package org.anvilpowered.anvil.core.spigot.implementation.block;

import org.anvilpowered.anvil.api.Interface.block.Block;
import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.core.spigot.helper.SpigotLocationHelper;
import org.bukkit.Material;

public class SpigotBlock implements Block {

    private org.bukkit.block.Block spigotBlock;

    private SpigotBlock(org.bukkit.block.Block block) {
        this.spigotBlock = block;
    }

    @Override
    public String getName() {
        return getKey();
    }

    @Override
    public String getKey() {
        return spigotBlock.getType().getKey().getKey();
    }

    @Override
    public Location getLocation() {
        return SpigotLocationHelper.fromSpigot(spigotBlock.getLocation());
    }

    @Override
    public void setType(String key) {
        spigotBlock.setType(Material.getMaterial(key.toUpperCase()));
    }

}
