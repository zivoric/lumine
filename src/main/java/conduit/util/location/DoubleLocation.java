package conduit.util.location;

public class DoubleLocation {
    private final double x, y, z;
    public DoubleLocation(double x, double y, double z) {
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

    public DoubleLocation add(double x, double y, double z) {
        return new DoubleLocation(this.x+x, this.y+y, this.z+z);
    }

    public DoubleLocation subtract(double x, double y, double z) {
        return new DoubleLocation(this.x-x, this.y-y, this.z-z);
    }

    public DoubleLocation multiply(double x, double y, double z) {
        return new DoubleLocation(this.x*x, this.y*y, this.z*z);
    }

    public DoubleLocation divide(double x, double y, double z) {
        return new DoubleLocation(this.x/x, this.y/y, this.z/z);
    }
}
