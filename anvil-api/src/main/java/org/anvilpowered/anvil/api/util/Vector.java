package org.anvilpowered.anvil.api.util;

public class Vector {

    protected double x;
    protected double y;
    protected double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return The vector after the equation
     */

    public Vector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     *
     * @param v The other vector to add to
     * @return (This) vector after the operation
     */

    public Vector add(Vector v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    public Vector sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector sub(Vector v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    public Vector multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vector multiply(Vector v) {
        this.x *= v.x;
        this.y *= v.y;
        this.z *= v.z;
        return this;
    }

    public double length() {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }

    public double lengthSquared() {
        return Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2);
    }

    /**
     * @param v Vector to compare to
       Get the distance between this vector and another vector.
    */

    public double distance(Vector v) {
        return Math.sqrt(Math.pow(x-v.x,2)+Math.pow(y-v.y,2)+Math.pow(z-v.z,2));
    }

    /*public float angle(Vector v) {

    }*/

}
