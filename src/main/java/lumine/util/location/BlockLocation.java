package lumine.util.location;

public class BlockLocation {
    private final int x, y, z;
    public BlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    public BlockLocation add(int x, int y, int z) {
        return new BlockLocation(this.x+x, this.y+y, this.z+z);
    }

    public BlockLocation subtract(int x, int y, int z) {
        return new BlockLocation(this.x-x, this.y-y, this.z-z);
    }

    public BlockLocation multiply(int x, int y, int z) {
        return new BlockLocation(this.x*x, this.y*y, this.z*z);
    }

    public BlockLocation divide(int x, int y, int z) {
        return new BlockLocation(this.x/x, this.y/y, this.z/z);
    }
}