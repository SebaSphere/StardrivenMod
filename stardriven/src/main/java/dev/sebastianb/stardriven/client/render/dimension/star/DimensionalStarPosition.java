package dev.sebastianb.stardriven.client.render.dimension.star;

import net.minecraft.util.math.Vec3d;

public class DimensionalStarPosition {
    double x;
    double y;
    double z;

    double pitch;
    double roll;
    double yaw;

    public DimensionalStarPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public DimensionalStarPosition(double x, double y, double z, double pitch, double roll, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
    }

    public void setCameraPositions(double x, double y, double z) {
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

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public double getYaw() {
        return yaw;
    }

    public Vec3d getVec3d() {
        return new Vec3d(x, y, z);
    }
}