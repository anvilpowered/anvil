package org.anvilpowered.anvil.api.Interface.world;

public class Position {

    private int x;
    private int y;
    private int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    };

    public double getZ() {
        return z;
    };

}
