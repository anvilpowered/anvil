package org.anvilpowered.anvil.core.spigot.helper;

import org.anvilpowered.anvil.api.Anvil;
import org.anvilpowered.anvil.api.Interface.world.Location;
import org.anvilpowered.anvil.core.spigot.implementation.world.SpigotWorld;
import org.bukkit.Bukkit;

public class SpigotLocationHelper {

    public static Location fromSpigot(org.bukkit.Location loc) {
        SpigotWorld world = Anvil.getAnvilInstance(loc.getWorld(), SpigotWorld.class);
        return new Location(world, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public static org.bukkit.Location fromAnvil(Location loc) {
        return new org.bukkit.Location(Bukkit.getWorld(loc.getWorld().getName()), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

}
