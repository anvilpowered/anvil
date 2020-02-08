package org.anvilpowered.anvil.api.Interface.world;

import com.google.inject.Inject;
import org.anvilpowered.anvil.api.Interface.block.Block;
import org.anvilpowered.anvil.api.util.SoundService;

public class Location {

    @Inject
    private SoundService soundService;

    private World world;
    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public Location(World world, double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.yaw = yaw;
        this.pitch = pitch;

        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public Block getBlock() {
        return world.getBlockAt((int)x,(int)y,(int)z);
    }

    public double getX() {
        return x;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Location sub(double x, double y, double z, float yaw, float pitch) {
        return new Location(this.world, this.x-x, this.y-y, this.z-z, this.yaw-yaw, this.pitch-pitch);
    }

    public Location add(double x, double y, double z, float yaw, float pitch) {
        return new Location(this.world, this.x+x, this.y+y, this.z+z, this.yaw+yaw, this.pitch+pitch);
    }

    public void playSound(String sound, float pitch, float volume) {
        soundService.playSound(sound, pitch, volume, this);
    }

}
