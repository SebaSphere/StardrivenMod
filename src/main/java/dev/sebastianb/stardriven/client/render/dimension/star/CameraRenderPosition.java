package dev.sebastianb.stardriven.client.render.dimension.star;

public class CameraRenderPosition {
    double x;
    double y;
    double z;

    public CameraRenderPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}